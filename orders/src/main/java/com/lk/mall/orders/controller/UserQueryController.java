package com.lk.mall.orders.controller;

import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	
	private static final Logger logger = LoggerFactory.getLogger(UserQueryController.class);

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

	@RequestMapping("/findOrderByOrderIdAsync")
	public Orders findOrderByOrderIdAsync(@RequestParam("orderId") String orderId) {
		return userQueryService.findOrderByOrderIdAsync(orderId);
	}
	
	@RequestMapping("/findOrderByOrderId")
	public Orders findOrderByOrderId(@RequestParam("orderId") String orderId) {
		return userQueryService.findOrderByOrderId(orderId);
	}
	
	@RequestMapping("/submit")
	public String submit() {
//		这个接口的log参见log，与application同路径
		String sessionId = UUID.randomUUID().toString();//用uuid模仿不同的用户的sessionId
		logger.info("start executeAsync:{}", sessionId);
		// 调用service层的任务
		userQueryService.executeAsync(sessionId);
		logger.info("end submit:{}", sessionId);
		return "success";
	}

}