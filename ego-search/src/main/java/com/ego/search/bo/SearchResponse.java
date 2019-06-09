package com.ego.search.bo;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Brand;
import com.ego.item.pojo.Category;
import com.ego.search.pojo.Goods;

import java.util.List;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/6/3
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
public class SearchResponse extends PageResult<Goods> {
    private List<Category> categories;
    private List<Brand> brands;


    public SearchResponse(Long total, Long totalPage, List<Goods> items, List<Category> categories, List<Brand> brands) {
        super(total, totalPage, items);
        this.categories = categories;
        this.brands = brands;
    }

    public List<Category> getCategories() {
        return categories;
    }

    public void setCategories(List<Category> categories) {
        this.categories = categories;
    }

    public List<Brand> getBrands() {
        return brands;
    }

    public void setBrands(List<Brand> brands) {
        this.brands = brands;
    }
}
