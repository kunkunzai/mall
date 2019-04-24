package com.lk.mall.orders.service;

import org.springframework.data.domain.Page;

import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;

/**
 * 用户查询订单service
 * @author kun
 *
 */
public interface IUserQueryService {
	
	CornerMarkVO findCornerMarkByUserId(final Long userId);
	
	Page<Orders> findOrderListByUserId(final Long userId, final Integer status, final Integer page, final Integer size);
	
	Orders findOrderByOrderId(final String orderId);

}
