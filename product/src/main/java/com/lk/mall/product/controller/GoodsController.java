package com.lk.mall.product.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.product.model.Goods;
import com.lk.mall.product.model.vo.GoodsVO;
import com.lk.mall.product.service.IGoodsService;


@RestController
public class GoodsController {
    
    @Autowired
    private IGoodsService goodsService;
    
    @RequestMapping("/saveGoods")
    public Integer saveGoods(@RequestBody Goods goods) {
        return goodsService.saveGoods(goods);
    }
    
    @RequestMapping("/findGoodsDetail")
    public List<GoodsVO> findGoodsDetail(@RequestParam List<Long> goodsIds) {
        return goodsService.findGoodsDetail(goodsIds);
    }

}
