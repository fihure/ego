package com.ego.item.service;

import com.ego.common.pojo.PageResult;
import com.ego.item.mapper.BrandMapper;
import com.ego.item.pojo.Brand;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.List;

@Service
public class BrandService {
    @Autowired
    private BrandMapper brandMapper;
    @Transactional(readOnly = true)
    public PageResult<Brand> queryBrandByPageAndSort(
      Integer page,Integer rows,String sortBy,Boolean desc,String key)
    {
        //开始分页
        PageHelper.startPage(page,rows);
        //过滤
        Example example = new Example(Brand.class);
        if(StringUtils.isNotBlank(key)){
            example.createCriteria().andLike("name","%"+key+"%")
                    .orEqualTo("letter",key);
        }
        if(StringUtils.isNotBlank(sortBy)){
            //排序
            String orderByClause = sortBy + (desc?" desc":" asc");
            example.setOrderByClause(orderByClause);
        }
        //查询
        Page<Brand> pageInfo = (Page<Brand>) brandMapper.selectByExample(example);
        //返回结果
        return new PageResult<>(pageInfo.getTotal(),pageInfo);
    }
    @Transactional
    public void save(Brand brand, List<Long> cids) {
        //保存品牌
        brandMapper.insertSelective(brand);
        //保存品牌和类别中间表
        if(cids!=null){
            for (Long cid:cids) {
                brandMapper.insertBrandCategory(cid,brand.getId());
            }
        }

    }
    @Transactional
    public Integer delete(Long bid) {
        int i = brandMapper.deleteByPrimaryKey(bid);
        if(i<=0){
            return -1;
        }
        return 1;
    }
    @Transactional
    public Integer update(Brand brand, List<Long> cids) {
        //更改品牌表
        brandMapper.updateByPrimaryKeySelective(brand);
        //更改品牌类别中间表
        for (Long cid:cids) {
            brandMapper.updateBrandCategory(cid,brand.getId());
        }
        return 1;
    }
    public Brand getById(Long brandId) {
        return brandMapper.selectByPrimaryKey(brandId);
    }

    public List<Brand> queryListByCid(Long cid) {
        return brandMapper.queryListByCid(cid);
    }
    public Brand queryBrandByBid(Long bid) {
        return brandMapper.selectByPrimaryKey(bid);
    }

    public List<Brand> queryBrandByIds(List<Long> ids) {
        return this.brandMapper.selectByIdList(ids);
    }
}
