package com.lk.mall.orders.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;
import com.lk.mall.orders.service.IUserQueryService;

@RestController
public class UserQueryController {

	@Autowired
	IUserQueryService userQueryService;

    @RequestMapping("/findCornerMarkByUserId")
    public CornerMarkVO findCornerMarkByUserId(@RequestParam("userId") Long userId) {
        return userQueryService.findCornerMarkByUserId(userId);
    }
    
	@RequestMapping("/findOrderListByUserId")
	public Page<Orders> findOrderListByUserId(
			@RequestParam("userId") Long userId, 
			@RequestParam("status") Integer status,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "2") Integer size) {

		return userQueryService.findOrderListByUserId(userId, status, page, size);
	}

	@RequestMapping("/findOrderByOrderId")
	public Orders findOrderByOrderId(@RequestParam("orderId") String orderId) {
		return userQueryService.findOrderByOrderId(orderId);
	}

}