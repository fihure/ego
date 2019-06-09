package com.ego.item.service;

import com.ego.item.mapper.SpecificationMapper;
import com.ego.item.pojo.Specification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tk.mybatis.mapper.entity.Example;

@Service
public class SpecificationService {
    @Autowired
    private SpecificationMapper specificationMapper;
    public Specification queryById(Long id) {
        return this.specificationMapper.selectByPrimaryKey(id);
    }

    public Integer update(Specification specification) {
        int i = specificationMapper.updateByPrimaryKeySelective(specification);
        return i;
    }

    public Integer save(Specification specification) {

        int i = specificationMapper.insertSelective(specification);
        return i;
    }
}
