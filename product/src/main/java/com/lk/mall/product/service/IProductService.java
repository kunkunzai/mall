package com.lk.mall.product.service;

import java.util.List;

import org.springframework.data.domain.Page;

import com.lk.mall.product.model.Product;

public interface IProductService {

	Product findById(Long id);
	
	List<Product> findAllById(List<Long> ids);

	Page<Product> findByShopId(final Long shopId, final Integer status, Integer page, Integer size);

	Product save(Product item);

}
