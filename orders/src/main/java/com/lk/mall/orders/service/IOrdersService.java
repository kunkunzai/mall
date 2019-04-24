package com.lk.mall.orders.service;

import org.springframework.data.domain.Page;

import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;
import com.lk.mall.orders.model.vo.PaymentVO;
import com.lk.mall.orders.model.vo.SettlementVO;

public interface IOrdersService {
	
	SettlementVO settle(SettlementVO settlementVO);

	Orders save(Orders orders);
	
	Integer pay(PaymentVO paymentVO);

	Orders findByOrderId(String orderId);
	
	Page<Orders> findByUserId(final Long userId, final Integer status, Integer page, Integer size);
	
	Integer cancelOrder(final Long userId, final String orderId);
	
	Integer deleteOrder(final Long userId, final String orderId);
	
	CornerMarkVO findCornerMarkByUserId(Long userId);

}
