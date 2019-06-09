package com.ego.item.mapper;

import com.ego.item.pojo.Brand;
import org.apache.ibatis.annotations.*;
import tk.mybatis.mapper.additional.idlist.SelectByIdListMapper;

import java.util.List;

@Mapper
public interface BrandMapper extends tk.mybatis.mapper.common.Mapper<Brand>, SelectByIdListMapper<Brand,Long> {
    /**
     * 保存品牌和
     * @param cid
     * @param bid
     */
    @Insert("insert into tb_category_brand (brand_id,category_id) values(#{bid},#{cid})")
    void insertBrandCategory(@Param("cid") Long cid, @Param("bid")Long bid);
    @Update("update tb_category_brand set category_id=#{bid} where brand_id=#{cid}")
    void updateBrandCategory(@Param("cid") Long cid, @Param("bid")Long bid);
    @Select("select b.id,b.name,b.image,b.letter from tb_brand b join tb_category_brand cb on b.id = cb.brand_id where cb.category_id = #{cid}")
    List<Brand> queryListByCid(@Param("cid") Long cid);
}
