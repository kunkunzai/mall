package com.lk.mall.orders.controller;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.orders.model.OrderItem;
import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;
import com.lk.mall.orders.model.vo.OrdersVO;
import com.lk.mall.orders.model.vo.PaymentVO;
import com.lk.mall.orders.model.vo.SettlementVO;
import com.lk.mall.orders.service.IOrdersService;

@RestController
public class OrderController {

	@Autowired
	IOrdersService ordersService;

	@Autowired
	private DozerBeanMapper mapper;

	@GetMapping(value = "/settleOrder")
	public SettlementVO settleOrder(@Valid @RequestBody SettlementVO settlementVO) {
		SettlementVO newSettlementVO = ordersService.settle(settlementVO);
		return newSettlementVO;
	}

	@GetMapping(value = "/saveOrder")
	public Orders saveOrder(@Valid @RequestBody OrdersVO ordersVO) {
		Orders orders = generateOrder(ordersVO);
		Orders newOrder = ordersService.save(orders);
		return ordersService.findByOrderId(newOrder.getOrderId());
	}

	private Orders generateOrder(OrdersVO ordersVO) {
		Orders orders = mapper.map(ordersVO, Orders.class);
		List<OrderItem> orderItemList = new ArrayList<>();
		ordersVO.getShopList().forEach(x -> {
			x.getProductList().stream().forEach(y -> {
				orderItemList.add(mapper.map(y, OrderItem.class));
			});
		});
		orders.setOrderItemList(orderItemList);
		if (ordersVO.getShopList().size() == 1) {
			orders.setSplitFlag(100);
			orders.setShopId(ordersVO.getShopList().get(0).getShopId());
		} else {
			orders.setSplitFlag(200);
		}
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		orders.setParentOrderId("100");
		LocalDateTime now = LocalDateTime.now(ZoneOffset.of("+8"));
		orders.setOrderId(uuid);
		orders.setOrderTime(now);
		orders.setCreateTime(now);
		orders.getOrderItemList().forEach(x -> {
			x.setOrderId(uuid);
			x.setCreateTime(now);
		});
		return orders;
	}

	@RequestMapping("/payOrder")
	public Integer payOrder(@RequestBody PaymentVO paymentVO) {
		return ordersService.pay(paymentVO);
	}

	@RequestMapping("/findByOrderId")
	public Orders findByOrderId(@RequestParam("orderId") String orderId) {
		return ordersService.findByOrderId(orderId);
	}

	@RequestMapping("/findByUserId")
	public Page<Orders> findByUserId(
			@RequestParam("userId") Long userId, 
			@RequestParam("status") Integer status,
			@RequestParam(value = "page", defaultValue = "0") Integer page,
			@RequestParam(value = "size", defaultValue = "2") Integer size) {

		return ordersService.findByUserId(userId, status, page, size);
	}
	
	
    @RequestMapping("/findCornerMarkByUserId")
    public CornerMarkVO findCornerMarkByUserId(@RequestParam("userId") Long userId) {
        return ordersService.findCornerMarkByUserId(userId);
    }
	
    @RequestMapping("/cancelOrder")
    public Integer cancelOrder(@RequestParam("userId") Long userId, @RequestParam("orderId") String orderId) {
        return ordersService.cancelOrder(userId, orderId);
    }
	
    @RequestMapping("/deleteOrder")
    public Integer deleteOrder(@RequestParam("userId") Long userId, @RequestParam("orderId") String orderId) {
        return ordersService.deleteOrder(userId, orderId);
    }

}