package com.lk.mall.orders.controller;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.orders.model.OrderItem;
import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.OrdersVO;
import com.lk.mall.orders.model.vo.PaymentVO;
import com.lk.mall.orders.model.vo.SettlementVO;
import com.lk.mall.orders.service.IUserDisposeService;
import com.lk.mall.orders.service.IUserQueryService;

@RestController
public class UserDisposeController {

	@Autowired
	IUserDisposeService userDisposeService;
	@Autowired
	IUserQueryService userQueryService;

	@Autowired
	private DozerBeanMapper mapper;

	/**
	 * 结算订单
	 * @param settlementVO
	 * @return
	 */
	@RequestMapping(value = "/settleOrder")
	public SettlementVO settleOrder(@Valid @RequestBody SettlementVO settlementVO) {
		SettlementVO newSettlementVO = userDisposeService.settle(settlementVO);
		return newSettlementVO;
	}

	/**
	 * 提交订单
	 * @param ordersVO
	 * @return
	 */
	@RequestMapping(value = "/saveOrder")
	public Orders saveOrder(@Valid @RequestBody OrdersVO ordersVO) {
		Orders orders = generateOrder(ordersVO);
		Orders newOrder = userDisposeService.save(orders);
		return userQueryService.findOrderByOrderId(newOrder.getOrderId());
	}

	/**
	 * 生成订单信息
	 * @param ordersVO
	 * @return
	 */
	private Orders generateOrder(OrdersVO ordersVO) {
		Orders orders = mapper.map(ordersVO, Orders.class);
		List<OrderItem> orderItemList = new ArrayList<>();
		ordersVO.getShopList().forEach(x -> {
			x.getProductList().stream().forEach(y -> {
				orderItemList.add(mapper.map(y, OrderItem.class));
			});
		});
		orders.setOrderItemList(orderItemList);
		//根据商家个数确定是否拆单
		if (ordersVO.getShopList().size() == 1) {
			orders.setSplitFlag(100);
			orders.setShopId(ordersVO.getShopList().get(0).getShopId());
		} else {
			orders.setSplitFlag(200);
		}
//		简单起见,用UUID生成订单号
		String uuid = UUID.randomUUID().toString().replaceAll("-", "");
		orders.setParentOrderId("100");
		LocalDateTime now = LocalDateTime.now();
		orders.setOrderId(uuid);
		orders.setOrderTime(now);
		orders.setCreateTime(now);
		orders.getOrderItemList().forEach(x -> {
			x.setOrderId(uuid);
			x.setCreateTime(now);
		});
		return orders;
	}

	/**
	 * 支付订单
	 * @param paymentVO
	 * @return
	 */
	@RequestMapping("/payOrder")
	public Integer payOrder(@RequestBody PaymentVO paymentVO) {
		return userDisposeService.pay(paymentVO);
	}
	
	/**
	 * 取消订单
	 * @param userId
	 * @param orderId
	 * @return
	 */
    @RequestMapping("/cancelOrder")
    public Integer cancelOrder(@RequestParam("userId") Long userId, @RequestParam("orderId") String orderId) {
        return userDisposeService.cancelOrder(userId, orderId);
    }
	
    /**
     * 确认收货
     * @param userId
     * @param orderId
     * @return
     */
    @RequestMapping("/confirmDelivery")
    public Integer confirmDelivery(@RequestParam("userId") Long userId, @RequestParam("orderId") String orderId) {
        return userDisposeService.confirmDelivery(userId, orderId);
    }
    
    /**
     * 删除订单
     * @param userId
     * @param orderId
     * @return
     */
    @RequestMapping("/deleteOrder")
    public Integer deleteOrder(@RequestParam("userId") Long userId, @RequestParam("orderId") String orderId) {
        return userDisposeService.deleteOrder(userId, orderId);
    }

}