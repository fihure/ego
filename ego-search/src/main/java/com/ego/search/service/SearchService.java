package com.ego.search.service;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.BO.SpuBO;
import com.ego.item.pojo.Brand;
import com.ego.item.pojo.Category;
import com.ego.item.pojo.Sku;
import com.ego.search.bo.SearchRequest;
import com.ego.search.bo.SearchResponse;
import com.ego.search.client.BrandClient;
import com.ego.search.client.CategoryClient;
import com.ego.search.client.GoodsClient;
import com.ego.search.dao.GoodsRespository;
import com.ego.search.pojo.Goods;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.bucket.terms.LongTerms;
import org.elasticsearch.search.aggregations.bucket.terms.StringTerms;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.elasticsearch.core.aggregation.AggregatedPage;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 〈〉
 *
 * @author coach tam
 * @email 327395128@qq.com
 * @create 2019/6/3
 * @since 1.0.0
 * 〈坚持灵活 灵活坚持〉
 */
@Slf4j
@Service
public class SearchService {

    @Autowired
    private GoodsClient goodsClient;

    @Autowired
    private CategoryClient categoryClient;

    @Autowired
    private BrandClient brandClient;

    @Autowired
    private GoodsRespository goodsRespository;

    private ObjectMapper mapper = new ObjectMapper();

    public Goods buildGoods(SpuBO spuBO) {
        Goods goods = new Goods();
        try
        {
            goods.setSubTitle(spuBO.getSubTitle());

            List<Sku> skuList = goodsClient.querySkuListBySpuId(spuBO.getId()).getBody();
            //skuList --> json str
            String skus = mapper.writeValueAsString(skuList);
            goods.setSkus(skus);
            List<Long> prices = new ArrayList<>();
            skuList.forEach(sku->{
                prices.add(sku.getPrice());
            });
            goods.setPrice(prices);
            goods.setCreateTime(spuBO.getCreateTime());
            goods.setCid1(spuBO.getCid1());
            goods.setCid2(spuBO.getCid2());
            goods.setCid3(spuBO.getCid3());
            goods.setBrandId(spuBO.getBrandId());
            //标题  类别  品牌
            String cnames = categoryClient.queryNamesByCids(Arrays.asList(spuBO.getCid1(), spuBO.getCid2(), spuBO.getCid3())).getBody();
            String bname = brandClient.queryBrandByBid(spuBO.getBrandId()).getBody().getName();
            goods.setAll(spuBO.getTitle()  + " " +cnames +" "+bname);

            //可以用来搜索的动态属性 specs<String,Object>
            Map<String, Object> specs = new HashMap<>();

            //获取specifications --> List<Map<String,Object>>  -->循环遍历每个params --> seachable:true-->存入specs中
            String specifications = goodsClient.querySpuDetailBySpuId(spuBO.getId()).getBody().getSpecifications();
            List<Map<String,Object>> specList = mapper.readValue(specifications,new TypeReference<List<Map<String,Object>>>(){});

            specList.forEach(spec->{
                    List<Map<String,Object>> params = (List<Map<String,Object>>)spec.get("params");
                    params.forEach(param->{
                        if((boolean)param.get("global"))
                        {
                            if((boolean)param.get("searchable"))
                            {
                                specs.put(param.get("k").toString(),param.get("v"));
                            }
                        }
                    });
            });

            goods.setSpecs(specs);
            goods.setId(spuBO.getId());
        }catch (Exception e)
        {
            log.error("spu转goods发生错误:{}",e.getMessage());
        }
        return goods;
    }

    public SearchResponse search(SearchRequest searchRequest) {
        String key = searchRequest.getKey();
        Integer page = searchRequest.getPage();
        if(StringUtils.isBlank(key)){
                return null;
        }
        NativeSearchQueryBuilder searchQuery = new NativeSearchQueryBuilder();
        //分页查询all
        searchQuery.withQuery(QueryBuilders.matchQuery("all",key));
        //分页查询
        searchQuery.withPageable(PageRequest.of(page-1,searchRequest.getSize()));
        //聚合类别和品牌
        searchQuery.addAggregation(AggregationBuilders.terms("分类").field("cid3"));
        searchQuery.addAggregation(AggregationBuilders.terms("品牌").field("brandId"));
        AggregatedPage<Goods> pageInfo = (AggregatedPage<Goods>)goodsRespository.search(searchQuery.build());
        //获取聚合结果集
        List<Category> categories=getCategoryAggResult(pageInfo);
        List<Brand> brands=getBrandAggResult(pageInfo);
        //返回结果
        return new SearchResponse(pageInfo.getTotalElements(),Long.valueOf(pageInfo.getTotalPages()),pageInfo.getContent(),categories,brands);
    }

    private List<Brand> getBrandAggResult(AggregatedPage<Goods> pageInfo) {
        LongTerms terms = (LongTerms) pageInfo.getAggregation("品牌");
        List<Long> bids = terms.getBuckets().stream().map(bucket -> bucket.getKeyAsNumber().longValue()).collect(Collectors.toList());
        return brandClient.queryBrandByIds(bids);
    }

    private List<Category> getCategoryAggResult(AggregatedPage<Goods> pageInfo) {
        //将聚合桶转换成categoryIds
        LongTerms stringTerms = (LongTerms) pageInfo.getAggregation("分类");
        List<Long> ids = stringTerms.getBuckets().stream().map(bucket -> (Long)bucket.getKey()).collect(Collectors.toList());
        //根据Ids查询出对应的类别名字
        String[] cnames = categoryClient.queryNamesByCids(ids).getBody().split(",");
        AtomicInteger i= new AtomicInteger();
        return ids.stream().map(id->{
            Category category = new Category();
            category.setId(id);
            category.setName(cnames[i.getAndIncrement()]);
            return category;
        }).collect(Collectors.toList());
    }
}
