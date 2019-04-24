package com.lk.mall.orders.service;

import org.springframework.data.domain.Page;

import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;
import com.lk.mall.orders.model.vo.ShipmentsVO;

public interface IShopService {
	
	
	CornerMarkVO findCornerMarkByShopId(Long shopId);
	
	Page<Orders> findByShopId(final Long shopId, final Integer status, Integer page, Integer size);
	
	Integer shipments(ShipmentsVO shipmentVOo);

}
