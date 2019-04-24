package com.lk.mall.orders.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.orders.model.vo.ShipmentsVO;
import com.lk.mall.orders.service.IShopDisposeService;

@RestController
public class ShopDisposeController {

	@Autowired
	IShopDisposeService shopDisposeService;
    
    @RequestMapping("/shipments")
    public Integer shipments(@Valid @RequestBody ShipmentsVO shipmentVO) {
        return shopDisposeService.shipments(shipmentVO);
    }

}