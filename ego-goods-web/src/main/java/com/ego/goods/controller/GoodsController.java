package com.ego.goods.controller;

import com.ego.goods.service.GoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Controller
@RequestMapping("/item")
public class GoodsController {
    @Autowired
    private GoodsService goodsService;

    @GetMapping("/{id}.html")
    public String item(@PathVariable(value = "id")Long id, Model model){
        Map<String,Object> modelMap=goodsService.loadModel(id);
        //把数据放入模型中
        model.addAllAttributes(modelMap);
        //异步生成静态页面
        goodsService.buildHtml(modelMap,id);
        return "item";
    }
}
