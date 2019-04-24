package com.lk.mall.orders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;
import com.lk.mall.orders.service.IShopQueryService;

@RestController
public class ShopQueryController {
	
	@Autowired
	IShopQueryService shopQueryService;

    @RequestMapping("/findCornerMarkByShopId")
    public CornerMarkVO findCornerMarkByShopId(@RequestParam("shopId") Long shopId) {
        return shopQueryService.findCornerMarkByShopId(shopId);
    }
    
    @RequestMapping("/findOrderListByShopId")
    public Page<Orders> findOrderListByShopId(
            @RequestParam("shopId") Long shopId, 
            @RequestParam("status") Integer status,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "2") Integer size) {

        return shopQueryService.findOrderListByShopId(shopId, status, page, size);
    }

}