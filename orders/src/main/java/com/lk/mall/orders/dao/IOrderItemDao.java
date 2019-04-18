package com.lk.mall.orders.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.orders.model.OrderItem;;


public interface IOrderItemDao extends JpaRepository<OrderItem, Long>, JpaSpecificationExecutor<OrderItem> {
	
	List<OrderItem> findByOrderId(String orderId);

}
