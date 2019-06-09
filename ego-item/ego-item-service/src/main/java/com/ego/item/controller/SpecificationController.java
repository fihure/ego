package com.ego.item.controller;

import com.ego.item.pojo.Specification;
import com.ego.item.service.SpecificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("spec")
public class SpecificationController {
    @Autowired
    private SpecificationService specificationService;
    @GetMapping("{id}")
    public ResponseEntity<String> querySpecificationByCategoryId(@PathVariable("id") Long id)
    {
        Specification spec = this.specificationService.queryById(id);
        if (spec == null) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
        return ResponseEntity.ok(spec.getSpecifications());
    }
    @PutMapping
    public ResponseEntity<Void> update(Specification specification){
        Integer saveNum = this.specificationService.update(specification);
        if(saveNum>0){
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PostMapping
    public ResponseEntity<Void> save(Specification specification){
        Integer saveNum = this.specificationService.save(specification);
        if(saveNum>0){
            return new ResponseEntity<>(HttpStatus.CREATED);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }
}
