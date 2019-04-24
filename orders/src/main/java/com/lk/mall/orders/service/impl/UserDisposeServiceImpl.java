package com.lk.mall.orders.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.orders.Exception.IllegalPriceException;
import com.lk.mall.orders.Exception.OrderNotExistException;
import com.lk.mall.orders.Exception.OrderStatusException;
import com.lk.mall.orders.Exception.ServicesNotConnectedException;
import com.lk.mall.orders.dao.IOrderItemDao;
import com.lk.mall.orders.dao.IOrdersDao;
import com.lk.mall.orders.feign.IProductService;
import com.lk.mall.orders.model.OrderItem;
import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.response.ProductServiceResponse;
import com.lk.mall.orders.model.vo.PaymentVO;
import com.lk.mall.orders.model.vo.SettlementVO;
import com.lk.mall.orders.service.IUserDisposeService;
import com.lk.mall.orders.utils.CollectorsUtils;

@Service
public class UserDisposeServiceImpl implements IUserDisposeService {

	@Autowired
	IProductService productService;

	@Autowired
	private IOrdersDao ordersDao;
	@Autowired
	private IOrderItemDao orderItemDao;
	
	@Override
	public SettlementVO settle(SettlementVO settlementVO) {
		List<ProductServiceResponse> productServiceResponse = getProductServiceResponse(settlementVO);
		populateSettlementVO(settlementVO, productServiceResponse);
		return settlementVO;
	}
	
	private List<ProductServiceResponse> getProductServiceResponse(SettlementVO settlementVO) {
		List<Long> list = new ArrayList<>();
		settlementVO.getShopList().forEach(x -> x.getProductList().forEach(y -> list.add(y.getProductId())));
		List<ProductServiceResponse> productList = productService.findAllByProductId(list);
		Optional.ofNullable(productList).orElseThrow(() -> new ServicesNotConnectedException());
//		System.out.println(productList.toString());
		return productList;
	}

	private void populateSettlementVO(SettlementVO settlementVO, List<ProductServiceResponse> productServiceResponse) {
		settlementVO.getShopList().forEach(x -> {
			x.getProductList().forEach(y -> {
				productServiceResponse.forEach(z -> {
					if (y.getProductId() == z.getId()) {
						y.setProductImage(z.getSmallImage());
						y.setProductMoney(z.getSalePrice());
						y.setProductName(z.getName());
						y.setProductSubtitle(z.getDescription());
						y.setProductType(z.getType());
						y.setTotalMoney(y.getProductAllMoney());
					}
				});
			});
			x.setTotalMoney(x.getShopMoney());
		});
		settlementVO.setOrderMoney(settlementVO.getShopAllMoney());
	}

	@Override
	@Transactional
	public Orders save(Orders orders) {
//		//	调用product服务得到product详情
		List<ProductServiceResponse> productServiceResponseList = getProductServiceResponse(orders);
//		//	生成order
		resolveOrderItem(orders, productServiceResponseList);
//		//	验证前端传来金额和后端计算的金额是否一致
		verifyAmount(orders, productServiceResponseList);
//		//  创建预支付信息
		createPreparePayment(orders);
//		//	将剩余没填充的信息填充进order
		resolveOrder(orders);

//		insert date base
		orderItemDao.saveAll(orders.getOrderItemList());
		ordersDao.save(orders);
		return orders;
	}

	// 调用product服务得到product详情
	private List<ProductServiceResponse> getProductServiceResponse(Orders orders) {
		List<Long> list = new ArrayList<>();
		orders.getOrderItemList().stream().forEach(x -> list.add(x.getProductId()));
		List<ProductServiceResponse> productList = productService.findAllByProductId(list);
		Optional.ofNullable(productList).orElseThrow(() -> new ServicesNotConnectedException());
		return productList;
	}

//	填充orderItem
	private void resolveOrderItem(Orders orders, List<ProductServiceResponse> productServiceResponseList) {
		productServiceResponseList.stream().forEach(x -> {
			orders.getOrderItemList().stream().forEach(y -> {
				if (x.getId() == y.getProductId()) {
					y.setProductName(x.getName());
					y.setProductImage(x.getSmallImage());
					y.setProductSubtitle(x.getDescription());
					y.setProductType(x.getType());
					y.setShopId(x.getShopId());
					y.setShopName(x.getShopName());
				}
			});
		});
	}

//	验证前端传来金额和后端计算的金额是否一致
    private void verifyAmount(Orders orders, List<ProductServiceResponse> productServiceResponse) {
        BigDecimal expectantTotalPrice = BigDecimal.ZERO;
        for (ProductServiceResponse product : productServiceResponse) {
            for (OrderItem orderItem : orders.getOrderItemList()) {
                if (product.getId() == orderItem.getProductId()) {
                    if (product.getSalePrice().compareTo(orderItem.getProductMoney()) != 0) {
                        System.err.println("商品:" + product.getId() + " 期盼的金额：" + product.getSalePrice() + ",实际的价格："+ orderItem.getProductMoney());
                        throw new IllegalPriceException(product.getSalePrice(), orderItem.getProductMoney());
                    }
                    expectantTotalPrice = expectantTotalPrice.add(orderItem.getProductMoney().multiply(BigDecimal.valueOf(orderItem.getQuantity())));
                }
            }
        }
        if (orders.getOrderMoney().compareTo(expectantTotalPrice) != 0) {
            System.err.println("期盼的总金额：" + expectantTotalPrice + ",实际的总价格：" + orders.getOrderMoney());
            throw new IllegalPriceException(expectantTotalPrice, orders.getOrderMoney());
        }
    }

//	创建预支付信息
	private void createPreparePayment(Orders orders) {
		//TODO:
	}

//	将剩余没填充的信息填充进order
	private void resolveOrder(Orders orders) {
		orders.setIsDelete(1);
		orders.setOrderStatus(5);
	}

	@Override
	public Integer pay(PaymentVO paymentVO) {
		Orders orders = verifyOrder(paymentVO);
		orders.setOrderStatus(8);
		orders.setTradeId(paymentVO.getTradeId());
		orders.setPayTime(LocalDateTime.now());
		ordersDao.saveAndFlush(orders);
		payFinish(orders, paymentVO);
		if (orders.getSplitFlag() == 200) {
			separateOrders(orders);
		}
		return 200;
	}
	
	private Orders verifyOrder(PaymentVO paymentVO) {
		String orderId = paymentVO.getOrderId();
		Orders orders = ordersDao.findByOrderId(orderId);
		// 验证订单是否存在
		if (null == orders) {
			createRefundPay(orders, paymentVO);
			throw new OrderNotExistException(orderId);
		}
		// 验证订单状态是否为待付款
		if (orders.getOrderStatus() != 5) {
			createRefundPay(orders, paymentVO);
			throw new OrderStatusException(orderId);
		}
		// 验证订单金额是否被篡改
		if (orders.getOrderMoney().compareTo(paymentVO.getOrderMoney()) != 0) {
			createRefundPay(orders, paymentVO);
			throw new IllegalPriceException(orders.getOrderMoney(), paymentVO.getOrderMoney());
		}
		return orders;
	}

	/**
	 * 根据不同的商家进行拆单
	 * @param orders
	 */
	@Transactional
	private void separateOrders(Orders orders) {
		System.err.println("执行拆单操作!");
		List<OrderItem> productList = orderItemDao.findByOrderId(orders.getOrderId());
//        map的key为shopID,value为该shop的totalMoney
		Map<Long, BigDecimal> map = productList.stream().collect(Collectors.groupingBy(OrderItem::getShopId,
				CollectorsUtils.summingBigDecimal(OrderItem::getTotalMoney)));
		map.entrySet().forEach(x -> System.out.println("商家 ：" + x.getKey() + "，总金额：" + x.getValue()));
		List<Orders> ordersList = new ArrayList<>(map.size());
		map.entrySet().forEach(x -> {
			// TODO:克隆速度更快
			Orders newOrders = new Orders();
			newOrders.setParentOrderId(orders.getOrderId());
			String uuid = UUID.randomUUID().toString().replaceAll("-", "");
			newOrders.setOrderId(uuid);
			newOrders.setShopId(x.getKey());
			newOrders.setOrderMoney(x.getValue());
			newOrders.setOrderWay(orders.getOrderWay());
			newOrders.setOrderStatus(8);
			newOrders.setReceiver(orders.getReceiver());
			newOrders.setUserId(orders.getUserId());
			newOrders.setMobile(orders.getMobile());
			newOrders.setOrderTime(LocalDateTime.now());
			newOrders.setPayTime(LocalDateTime.now());
			newOrders.setTradeId(orders.getTradeId());
			newOrders.setAddress(orders.getAddress());
			newOrders.setSplitFlag(100);
			newOrders.setIsDelete(1);
			newOrders.setCreateTime(orders.getCreateTime());
			ordersList.add(newOrders);
			productList.forEach(y -> {
				if (x.getKey() == y.getShopId()) {
					y.setOrderId(uuid);
				}
			});
		});
		orders.setSplitFlag(300);
		ordersDao.save(orders);
		ordersDao.saveAll(ordersList);
		orderItemDao.saveAll(productList);
	}

	private void payFinish(Orders orders, PaymentVO paymentVO) {

	}

	private void createRefundPay(Orders orders, PaymentVO paymentVO) {

	}
	
    @Override
    public Integer cancelOrder(Long userId, String orderId) {
        Orders orders = ordersDao.findByOrderId(orderId);
        if (userId != orders.getUserId()) {
            System.out.println("不是本人操作");
            return 100;
        }
        if (2 == orders.getOrderStatus()) {
            System.out.println("订单已取消");
            return 100;
        }
        if (5 != orders.getOrderStatus()) {
            System.out.println("订单不可取消");
            return 100;
        }
        orders.setOrderStatus(2);
        ordersDao.saveAndFlush(orders);
        return 200;
    }

    @Override
    public Integer deleteOrder(Long userId, String orderId) {
        Orders orders = ordersDao.findByOrderId(orderId);
        if (userId != orders.getUserId()) {
            System.out.println("不是本人操作");
            return 100;
        }
        if (2 != orders.getOrderStatus() && 20 != orders.getOrderStatus()) {
            System.out.println("目前不可删除");
            return 100;
        }
        orders.setIsDelete(2);
        ordersDao.saveAndFlush(orders);
        return 200;
    }

}