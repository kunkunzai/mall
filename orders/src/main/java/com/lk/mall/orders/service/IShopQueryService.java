package com.lk.mall.orders.service;

import org.springframework.data.domain.Page;

import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;

public interface IShopQueryService {
	
	
	CornerMarkVO findCornerMarkByShopId(Long shopId);
	
	Page<Orders> findOrderListByShopId(final Long shopId, final Integer status, Integer page, Integer size);
	
}
