package com.ego.item.controller;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.BO.SpuBO;
import com.ego.item.pojo.Sku;
import com.ego.item.pojo.SpuDetail;
import com.ego.item.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *
 */
@RestController
@RequestMapping("/goods")
public class GoodsController {

    @Autowired
    private GoodsService goodsService;
    //    page?key=&saleable=1&page=1&rows=5
    @GetMapping("/spu/page")
    public ResponseEntity<PageResult<SpuBO>> page(
            @RequestParam(value = "key",required = false) String key,
            @RequestParam(value = "saleable") Boolean saleable,
            @RequestParam(value = "page",defaultValue = "1") Integer page,
            @RequestParam(value = "rows",defaultValue = "5") Integer rows
    )
    {
        PageResult<SpuBO> result = goodsService.page(key, saleable, page, rows);

        if(result==null)
        {
            return  ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }


    @PostMapping
    public ResponseEntity<Void> save(@RequestBody SpuBO spuBO)
    {
        goodsService.save(spuBO);

        return ResponseEntity.ok(null);
    }

    @GetMapping("/sku/list/{spuId}")
    public ResponseEntity<List<Sku>> querySkuListBySpuId(@PathVariable("spuId") Long spuId){
        List<Sku> result = goodsService.querySkuListBySpuId(spuId);

        if(result==null)
        {
            return  ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/spuDetail/{spuId}")
    public ResponseEntity<SpuDetail> querySpuDetailBySpuId(@PathVariable("spuId")Long spuId){
        SpuDetail result = goodsService.querySpuDetailBySpuId(spuId);

        if(result==null)
        {
            return  ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }

    @GetMapping("/spuBO/{spuId}")
    public ResponseEntity<SpuBO> queryGoodsById(@PathVariable(value = "spuId") Long spuId){
        SpuBO result = goodsService.queryGoodsById(spuId);

        if(result==null)
        {
            return  ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(result);
    }
}
