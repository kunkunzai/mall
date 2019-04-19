package com.lk.mall.product.service.impl;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.product.dao.IShopDao;
import com.lk.mall.product.model.Shop;
import com.lk.mall.product.service.IShopService;

@Service
public class ShopServiceImpl implements IShopService {

	@Autowired
	private IShopDao ShopDao;

	@Override
	public Shop save(Shop shop) {
		shop.setStatus(0);
		return ShopDao.save(shop);
	}

	@Override
	public Shop findById(Long id) {
		Optional<Shop> shop = ShopDao.findById(id);
		if (shop.isPresent()) {
			return shop.get();
		}
		return new Shop();
	}

	@Override
	public List<Shop> findAllById(List<Long> ids) {
		List<Shop> shopList = ShopDao.findAllById(ids);
		return shopList;
	}

}
