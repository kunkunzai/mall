package com.lk.mall.orders.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.lk.mall.orders.Exception.OrderNotExistException;
import com.lk.mall.orders.dao.IOrderItemDao;
import com.lk.mall.orders.dao.IOrdersDao;
import com.lk.mall.orders.model.OrderItem;
import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;
import com.lk.mall.orders.service.IUserQueryService;

@Service
public class UserQueryServiceImpl implements IUserQueryService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserQueryServiceImpl.class);

	@Autowired
	private IOrdersDao ordersDao;
	@Autowired
	private IOrderItemDao orderItemDao;
	
	@Autowired
	private Executor asyncServiceExecutor;
	
    @Override
	public CornerMarkVO findCornerMarkByUserId(Long userId) {
		List<Orders> orderList = ordersDao.findAll((root, query, criteriaBuilder) -> {
			Predicate uid = criteriaBuilder.equal(root.get("userId").as(Long.class), userId);
			CriteriaBuilder.In<Object> status = criteriaBuilder.in(root.get("orderStatus"));
			status.value(5);
			status.value(8);
			status.value(10);
			status.value(15);
			CriteriaBuilder.In<Object> split = criteriaBuilder.in(root.get("splitFlag"));
			split.value(100);
			split.value(200);
			query.where(criteriaBuilder.and(uid, status, split));
			return query.getRestriction();
		});
//        分组求和
		Map<Integer, Long> map = orderList.stream().collect(Collectors.groupingBy(Orders::getOrderStatus, Collectors.counting()));
		map.entrySet().forEach(x -> System.out.println("状态 ：" + x.getKey() + "，数量：" + x.getValue()));
		CornerMarkVO corner = new CornerMarkVO();
		map.entrySet().forEach(x -> {
			switch (x.getKey()) {
			case 5:
				corner.setCountWaitPay(x.getValue());
				break;
			case 8:
				corner.setCountWaitSend(x.getValue());
				break;
			case 10:
				corner.setCountWaitReceive(x.getValue());
				break;
			case 15:
				corner.setCountWaitEvaluate(x.getValue());
				break;
			default:
				break;
			}
		});
		return corner;
	}
    
	@Override
	public Page<Orders> findOrderListByUserId(Long userId, Integer status, Integer page, Integer size) {
//		传入分页和排序属性
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.DESC, "orderTime");
//		查找orders表
		Page<Orders> ordersPage = ordersDao.findAll((root, query, criteriaBuilder) -> {
			Predicate p0 = criteriaBuilder.equal(root.get("isDelete").as(Integer.class), 1);
			Predicate p1 = criteriaBuilder.equal(root.get("userId").as(Long.class), userId);
			Predicate p2 = criteriaBuilder.equal(root.get("orderStatus").as(Integer.class), status);
			if (status == 0 || status == 5) {
				CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("splitFlag"));
				in.value(100);
				in.value(200);
				if (status == 0) {
					query.where(criteriaBuilder.and(p0, p1, in));
				} else {
					query.where(criteriaBuilder.and(p0, p1, p2, in));
				}
			} else {
				Predicate p3 = criteriaBuilder.equal(root.get("splitFlag").as(Integer.class), 100);
				query.where(criteriaBuilder.and(p0, p1, p2, p3));
			}
			return query.getRestriction();
		}, pageable);

		if (ordersPage.getContent().size() == 0) {
			System.out.println("订单不存在");
			return ordersPage;
		}
		List<String> orderIds = ordersPage.stream().map(Orders::getOrderId).collect(Collectors.toList());
		List<OrderItem> orderItemList = orderItemDao.findAll((root, query, criteriaBuilder) -> {
			CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("orderId"));
			orderIds.stream().forEach(x -> in.value(x));
			query.where(criteriaBuilder.and(in));
			return query.getRestriction();
		});
		for (Orders orders : ordersPage) {
			List<OrderItem> newOrderItemList = new ArrayList<>();
			for (OrderItem orderItem : orderItemList) {
				if (orders.getOrderId().equals(orderItem.getOrderId())) {
					newOrderItemList.add(orderItem);
				}
			}
			orders.setOrderItemList(newOrderItemList);
		}
		return ordersPage;
	}

	@Override
	public Orders findOrderByOrderId(String orderId) {
		long startTime=System.currentTimeMillis();   //获取开始时间
		Orders orders = ordersDao.findByOrderId(orderId);
		orders.setOrderItemList(orderItemDao.findByOrderId(orderId));
		long endTime=System.currentTimeMillis(); //获取结束时间
        System.out.println("单线程程序运行时间： "+(endTime-startTime)+"ms");  
		return orders;
	}

	@Override
	public Orders findOrderByOrderIdAsync(String orderId) {
		long startTime = System.currentTimeMillis(); // 获取开始时间
		Orders orders = null;
		CompletableFuture<Orders> ordersFuture = CompletableFuture.supplyAsync(() -> {
			logger.info("ordersDao.findByOrderId,{},{}", orderId, Thread.currentThread().getId());
			return ordersDao.findByOrderId(orderId);
		}, asyncServiceExecutor).thenCombine(CompletableFuture.supplyAsync(() -> {//使用自己构造的线程池
			logger.info("orderItemDao.findByOrderId,{},{}", orderId, Thread.currentThread().getId());
			return orderItemDao.findByOrderId(orderId);
		}, asyncServiceExecutor), (x, y) -> {//使用自己构造的线程池
			x.setOrderItemList(y);
			return x;
		}).exceptionally(e -> {
			System.err.println("该订单不存在:" + e.getMessage());
			return null;
		});
		try {
			orders = ordersFuture.get(5, TimeUnit.SECONDS);
		} catch (InterruptedException | ExecutionException | TimeoutException e) {
			throw new OrderNotExistException(orderId);
		}
		long endTime = System.currentTimeMillis(); // 获取结束时间
		System.out.println("多线程程序运行时间： " + (endTime - startTime) + "ms");
		return orders;
	}
	
	@Override
	@Async("asyncServiceExecutor")
	public void executeAsync(String sessionId) {
		logger.info("start executeAsync:{},{}", sessionId, Thread.currentThread().getId());
		try {
			Thread.sleep(5000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.info("end executeAsync:{}", sessionId, Thread.currentThread().getId());
	}
}
