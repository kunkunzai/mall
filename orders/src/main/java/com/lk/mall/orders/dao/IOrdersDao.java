package com.lk.mall.orders.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.lk.mall.orders.model.Orders;

public interface IOrdersDao extends JpaRepository<Orders, Long>, JpaSpecificationExecutor<Orders> {
	
	Orders findByOrderId(String orderId);
	
	@Modifying
    @Query(value = "UPDATE Orders SET order_status = 2 WHERE order_status = 5 AND DATE_ADD(order_time, INTERVAL 30 MINUTE) < NOW()", nativeQuery=true)
	Integer cancelOrder();
	
    @Modifying
    @Query(value = "UPDATE orders SET order_status = 15 WHERE order_status = 10 AND DATE_ADD(user_receive_time, INTERVAL 15 DAY) < NOW()", nativeQuery = true)
    Integer confirmDelivery();

}
