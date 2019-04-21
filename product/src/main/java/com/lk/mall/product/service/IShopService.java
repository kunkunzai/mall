package com.lk.mall.product.service;

import java.util.List;

import com.lk.mall.product.model.Shop;

public interface IShopService {
	
	Shop findById(Long id);
	
	List<Shop> findAllById(List<Long> ids);
	
	Shop save(Shop shop);
	
}
