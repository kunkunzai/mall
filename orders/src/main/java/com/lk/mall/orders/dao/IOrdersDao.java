package com.lk.mall.orders.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.orders.model.Orders;

public interface IOrdersDao extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
	
	Orders findByOrderId(String orderId);
	
}
