package com.ego.item.controller;

import com.ego.item.pojo.Category;
import com.ego.item.service.CategoryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 根据parentId查询类目
     * @param pid
     * @return
     */
    @RequestMapping("list")
    public ResponseEntity<List<Category>> queryCategoryListByParentId(@RequestParam(value =
            "pid", defaultValue = "0") Long pid) {
        try {
            if (pid == null || pid.longValue() < 0){
// pid为null或者小于等于0，响应400
                return ResponseEntity.badRequest().build();
            }
// 执行查询操作
            List<Category> categoryList =
                    this.categoryService.queryCategoryListByParentId(pid);
            if (CollectionUtils.isEmpty(categoryList)){
// 返回结果集为空，响应404
                return ResponseEntity.notFound().build();
            }
// 响应200
            return ResponseEntity.ok(categoryList);
        } catch (Exception e) {
            e.printStackTrace();
        }
// 响应500
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
    }
    @GetMapping("bid/{bid}")
    public ResponseEntity<List<Category>> queryByBrandId(@PathVariable("bid") Long bid) {
        List<Category> list = this.categoryService.queryByBrandId(bid);
        if (list == null || list.size() < 1) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }

    @GetMapping("/cnames")
    public ResponseEntity<String> queryNamesByCids(@RequestParam("cids") List<Long> cids){
        List<Category> result = categoryService.getListByCids(cids);
        List<String> cnameList = result.stream().map(category -> category.getName()).collect(Collectors.toList());
        if (result == null || result.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(StringUtils.join(cnameList,","));
    }

    @GetMapping("/list/{cids}")
    public ResponseEntity<List<Category>> queryCategoryByIds(@RequestParam("cids")List<Long> cids){
        List<Category> result = categoryService.getListByCids(cids);
        if (result == null || result.size() == 0) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }
}
