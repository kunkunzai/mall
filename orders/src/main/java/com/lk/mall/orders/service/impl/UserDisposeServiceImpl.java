package com.lk.mall.orders.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.redisson.api.RAtomicLongAsync;
import org.redisson.api.RFuture;
import org.redisson.api.RList;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.orders.Exception.IllegalPriceException;
import com.lk.mall.orders.Exception.OrderNotExistException;
import com.lk.mall.orders.Exception.OrderStatusException;
import com.lk.mall.orders.Exception.ServicesNotConnectedException;
import com.lk.mall.orders.constant.RedisConstant;
import com.lk.mall.orders.dao.IOrderItemDao;
import com.lk.mall.orders.dao.IOrdersDao;
import com.lk.mall.orders.feign.IProductService;
import com.lk.mall.orders.model.OrderItem;
import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.Qualification;
import com.lk.mall.orders.model.response.ProductServiceResponse;
import com.lk.mall.orders.model.vo.PaymentVO;
import com.lk.mall.orders.model.vo.ProductVO;
import com.lk.mall.orders.model.vo.SettlementVO;
import com.lk.mall.orders.model.vo.ShopVO;
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
    @Autowired
    private RedissonClient redissonClient;
	
	@Override
	public SettlementVO settle(SettlementVO settlementVO) {
//	    得到商品信息
		List<ProductServiceResponse> productServiceResponseList = getProductServiceResponse(settlementVO);
//		填充结算信息
		populateSettlementVO(settlementVO, productServiceResponseList);
		return settlementVO;
	}
	
	private List<ProductServiceResponse> getProductServiceResponse(SettlementVO settlementVO) {
//	    取出所有商品ID
		List<Long> list = new ArrayList<>();
		settlementVO.getShopList().forEach(x -> x.getProductList().forEach(y -> list.add(y.getProductId())));
		List<ProductServiceResponse> productList = productService.findGoodsDetail(list);
		Optional.ofNullable(productList).orElseThrow(() -> new ServicesNotConnectedException());
//		System.out.println(productList.toString());
		return productList;
	}

	/**
	 * 填充结算信息
	 * @param settlementVO
	 * @param productServiceResponseList
	 */
    private void populateSettlementVO(SettlementVO settlementVO, List<ProductServiceResponse> productServiceResponseList) {
        BigDecimal orderMoney = BigDecimal.ZERO;
        for (ShopVO shopVO : settlementVO.getShopList()) {
            BigDecimal shopMoney = BigDecimal.ZERO;
            for (ProductVO productVO : shopVO.getProductList()) {
                for (ProductServiceResponse productServiceResponse : productServiceResponseList) {
                    if (productVO.getProductId() == productServiceResponse.getId()) {
                        productVO.setProductImage(productServiceResponse.getSmallImage());
                        productVO.setProductMoney(productServiceResponse.getPrice());
                        productVO.setProductName(productServiceResponse.getName());
                        productVO.setProductSubtitle(productServiceResponse.getDescription());
                        productVO.setProductType(productServiceResponse.getType());
                        productVO.setTotalMoney(productVO.getProductMoney().multiply(new BigDecimal(productVO.getQuantity())));
                        shopMoney = shopMoney.add(productVO.getTotalMoney());
                        shopVO.setShopName(productServiceResponse.getShopName());
                        shopVO.setShopType(productServiceResponse.getType());
                    }
                }
            }
            shopVO.setShopMoney(shopMoney);
            orderMoney = orderMoney.add(shopMoney);
        }
        settlementVO.setOrderMoney(orderMoney);
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
		populateOrder(orders);

		/**
		 * 用mq解决分布式事务?
		 * 1.插入DB
		 * 2.锁库存
		 * 3.更新购物车
		 */
//		insert date base
		orderItemDao.saveAll(orders.getOrderItemList());
		ordersDao.save(orders);
		return orders;
	}

	// 调用product服务得到product详情
	private List<ProductServiceResponse> getProductServiceResponse(Orders orders) {
		List<Long> list = new ArrayList<>();
		orders.getOrderItemList().stream().forEach(x -> list.add(x.getProductId()));
		List<ProductServiceResponse> productList = productService.findGoodsDetail(list);
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
					y.setProductMoney(x.getPrice());
					y.setShopType(x.getShopType());
					y.setTotalMoney(x.getPrice().multiply(new BigDecimal(y.getQuantity())));
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
                    if (product.getPrice().compareTo(orderItem.getProductMoney()) != 0) {
                        System.err.println("商品:" + product.getId() + " 期盼的金额：" + product.getPrice() + ",实际的价格："+ orderItem.getProductMoney());
                        throw new IllegalPriceException(product.getPrice(), orderItem.getProductMoney());
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
	private void populateOrder(Orders orders) {
		orders.setIsDelete(1);
		orders.setOrderStatus(5);
	}

	@Override
    @Transactional
	public Integer pay(PaymentVO paymentVO) {
		Orders orders = verifyOrder(paymentVO);
		orders.setOrderStatus(8);
		orders.setTradeId(paymentVO.getTradeId());
		orders.setPayTime(LocalDateTime.now());
		ordersDao.save(orders);
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
			LocalDateTime now = LocalDateTime.now();
			productList.forEach(y -> {
				if (x.getKey() == y.getShopId()) {
					y.setOrderId(uuid);
					y.setUpdateTime(now);
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
    @Transactional
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
    @Transactional
    public Integer confirmDelivery(Long userId, String orderId) {
        Orders orders = ordersDao.findByOrderId(orderId);
        if (userId != orders.getUserId()) {
            System.out.println("不是本人操作");
            return 100;
        }
        if (10 != orders.getOrderStatus()) {
            System.out.println("目前不可确认收货");
            return 100;
        }
        orders.setOrderStatus(15);
        orders.setUserReceiveTime(LocalDateTime.now());
        ordersDao.saveAndFlush(orders);
        return 200;
    }

    @Override
    @Transactional
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

    @Override
    public String freezeOrder(String userId, String productId, String activityId) {
        String key = RedisConstant.REDIS_LOCK_PREFIX + activityId + ":" + productId;
//      验证库存
        RAtomicLongAsync stock = redissonClient.getAtomicLong(key);
        System.out.println("抢购key:" + key + ",库存:" + stock);
        if (Integer.parseInt(stock.toString()) <= 0) {
            System.out.println("库存不足");
            return "库存不足";
        }
//      判断是否是再次抢购
        RList<Qualification> qualificationList = redissonClient.getList("activityId_" + activityId);
        for (Qualification qualification : qualificationList) {
            if (qualification.getUserId().equals(userId) && qualification.getProductId().equals(productId)) {
                System.out.println("不能重复抢购");
                return "不能重复抢购";
            }
        }
        boolean res = false;
        RLock lock = redissonClient.getLock("anyLock");
        while (true) {
//          执行抢购逻辑
            try {
//              上锁1秒
                lock.lock(1, TimeUnit.SECONDS);
//              看门狗,1秒后开始计时
//              只要客户端一旦加锁成功，就会启动一个watch dog看门狗，他是一个后台线程，会每隔1秒检查一下，如果客户端还持有锁key，那么就会不断的延长锁key的生存时间。
                res = lock.tryLock(0, 1, TimeUnit.SECONDS);
                if (res) {
                    if (Integer.parseInt(redissonClient.getAtomicLong(key).toString()) <= 0) {
                        System.out.println("库存不足");
                        return "库存不足";
                    }
                    RFuture<Long> decrementAndGetAsync = stock.decrementAndGetAsync();
                    System.out.println("抢购后库存:" + decrementAndGetAsync.get().longValue());
                    Qualification qualification = new Qualification(1, productId, userId);
                    List<Qualification> list = redissonClient.getList("activityId_" + activityId);
                    list.add(qualification);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
            return "成功";
        }
    }

}
