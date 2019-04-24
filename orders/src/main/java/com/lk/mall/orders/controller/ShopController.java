package com.lk.mall.orders.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;
import com.lk.mall.orders.model.vo.ShipmentsVO;
import com.lk.mall.orders.service.IShopService;

@RestController
public class ShopController {

	@Autowired
	IShopService shopService;

    @RequestMapping("/findCornerMarkByShopId")
    public CornerMarkVO findCornerMarkByShopId(@RequestParam("shopId") Long shopId) {
        return shopService.findCornerMarkByShopId(shopId);
    }
    
    @RequestMapping("/findByShopId")
    public Page<Orders> findByShopId(
            @RequestParam("shopId") Long shopId, 
            @RequestParam("status") Integer status,
            @RequestParam(value = "page", defaultValue = "0") Integer page,
            @RequestParam(value = "size", defaultValue = "2") Integer size) {

        return shopService.findByShopId(shopId, status, page, size);
    }
    
    @RequestMapping("/shipments")
    public Integer shipments(@Valid @RequestBody ShipmentsVO shipmentVO) {
        return shopService.shipments(shipmentVO);
    }

}