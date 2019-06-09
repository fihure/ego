package com.ego.item.controller;

import com.ego.common.pojo.PageResult;
import com.ego.item.pojo.Brand;
import com.ego.item.service.BrandService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("brand")
public class BrandController {

    @Autowired
    private BrandService brandService;
    @GetMapping("page")
    public ResponseEntity<PageResult<Brand>> queryBrandByPage(
            @RequestParam(value = "pageNo", defaultValue = "1") Integer page,
            @RequestParam(value = "pageSize", defaultValue = "5") Integer rows,
            @RequestParam(value = "sortBy", required = false) String sortBy,
            @RequestParam(value = "descending", defaultValue = "false") Boolean desc,
            @RequestParam(value = "key", required = false) String key)
    {
        PageResult<Brand> result = this.brandService.queryBrandByPageAndSort(page,rows,sortBy,desc,key);
        if(result == null || result.getItems().size()==0){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping
    public ResponseEntity<Void> save(Brand brand, @RequestParam("cids") List<Long> cids){
        brandService.save(brand,cids);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
    @PutMapping
    public ResponseEntity<Void> update(Brand brand, @RequestParam("cids") List<Long> cids){
        Integer update = brandService.update(brand, cids);
        if(update>0){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return null;
    }

    @DeleteMapping
    public ResponseEntity<Void> delete(@RequestParam("id") Long bid){
        Integer delNum = brandService.delete(bid);
        if(delNum>0){
            return ResponseEntity.status(HttpStatus.CREATED).build();
        }
        return null;
    }

    @GetMapping("/cid/{cid}")
    public ResponseEntity<List<Brand>> queryListByCid(
            @PathVariable(value = "cid")Long cid
    )
    {
        List<Brand> result = brandService.queryListByCid(cid);
        if(result==null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("/bid/{bid}")
    public ResponseEntity<Brand> queryBrandByBid(@PathVariable("bid") Long bid){
        Brand result = brandService.queryBrandByBid(bid);
        if(result==null)
        {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping("list")
    public ResponseEntity<List<Brand>> queryBrandByIds(@RequestParam("ids") List<Long> ids){
        List<Brand> list = this.brandService.queryBrandByIds(ids);
        if(list == null){
            new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(list);
    }
}
