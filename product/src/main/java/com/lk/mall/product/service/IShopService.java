package com.lk.mall.product.service;

import java.util.List;

import com.lk.mall.product.model.Shop;
import com.lk.mall.product.model.vo.ShopVO;

public interface IShopService {
	
	ShopVO findById(Long id);
	
	List<Shop> findAllById(List<Long> ids);
	
	Shop save(Shop shop);
	
}
