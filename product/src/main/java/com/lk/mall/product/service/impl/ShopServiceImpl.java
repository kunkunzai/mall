package com.lk.mall.product.service.impl;

import java.util.List;
import java.util.Optional;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.product.dao.IShopDao;
import com.lk.mall.product.model.Shop;
import com.lk.mall.product.model.vo.ShopVO;
import com.lk.mall.product.service.IShopService;

@Service
public class ShopServiceImpl implements IShopService {

	@Autowired
	private DozerBeanMapper mapper;

	@Autowired
	private IShopDao ShopDao;

	@Override
	public Shop save(Shop shop) {
		shop.setStatus(0);
		return ShopDao.save(shop);
	}

	@Override
	public ShopVO findById(Long id) {
		Optional<Shop> shop = ShopDao.findById(id);
		if (shop.isPresent()) {
			return mapper.map(shop.get(), ShopVO.class);
		}
		return new ShopVO();
	}

	@Override
	public List<Shop> findAllById(List<Long> ids) {
		List<Shop> shopList = ShopDao.findAllById(ids);
		return shopList;
	}

}
