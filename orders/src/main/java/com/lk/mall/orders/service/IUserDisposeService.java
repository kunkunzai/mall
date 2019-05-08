package com.lk.mall.orders.service;

import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.PaymentVO;
import com.lk.mall.orders.model.vo.SettlementVO;

public interface IUserDisposeService {
	
	SettlementVO settle(SettlementVO settlementVO);

	Orders save(Orders orders);
	
	Integer pay(PaymentVO paymentVO);
	
	Integer cancelOrder(final Long userId, final String orderId);
	
	Integer confirmDelivery(final Long userId, final String orderId);
	
	Integer deleteOrder(final Long userId, final String orderId);
	
	String freezeOrder(String userId, String productId, String activityId);
	
}
