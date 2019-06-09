package com.ego.item.service;

import com.ego.item.mapper.CategoryMapper;
import com.ego.item.pojo.Category;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    public List<Category> queryCategoryListByParentId(Long pid){
        Category category = new Category();
        category.setParentId(pid);
        return categoryMapper.select(category);
    }

    public List<Category> queryByBrandId(Long bid) {
        return this.categoryMapper.queryByBrandId(bid);
    }
    public List<Category> getListByCids(List<Long> cids) {
//        new Example(Category.class).createCriteria().andIn("cid", cids);
        return categoryMapper.selectByIdList(cids);
    }


}
