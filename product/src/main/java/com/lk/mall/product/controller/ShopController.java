package com.lk.mall.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.product.model.Shop;
import com.lk.mall.product.service.IShopService;

@RestController
public class ShopController {

	@Autowired
	private IShopService ShopService;

	@RequestMapping("/findByShopId")
	public Shop findByShopId(@RequestParam("shopId") Long shopId) {
		return ShopService.findById(shopId);
	}

	@RequestMapping("/saveShop")
	public Shop saveShop(@RequestBody Shop Shop) {
		System.err.println(Shop.toString());
		return ShopService.save(Shop);
	}
	
}
