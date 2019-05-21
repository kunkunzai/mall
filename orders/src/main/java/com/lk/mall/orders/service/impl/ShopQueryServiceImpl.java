package com.lk.mall.orders.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.ZSetOperations.TypedTuple;
import org.springframework.stereotype.Service;

import com.lk.mall.orders.constant.RedisConstant;
import com.lk.mall.orders.dao.IOrderItemDao;
import com.lk.mall.orders.dao.IOrdersDao;
import com.lk.mall.orders.model.OrderItem;
import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.CornerMarkVO;
import com.lk.mall.orders.model.vo.RankingListVO;
import com.lk.mall.orders.model.vo.RankingListVO.Ranking;
import com.lk.mall.orders.service.IShopQueryService;
import com.lk.mall.orders.utils.RedisUtil;

@Service
public class ShopQueryServiceImpl implements IShopQueryService {

    @Autowired
    private IOrdersDao ordersDao;
    @Autowired
    private IOrderItemDao orderItemDao;
    @Autowired
    private RedisUtil redisUtil;

    @Override
	public CornerMarkVO findCornerMarkByShopId(Long shopId) {
		List<Orders> orderList = ordersDao.findAll((root, query, criteriaBuilder) -> {
			Predicate sid = criteriaBuilder.equal(root.get("shopId").as(Long.class), shopId);
			CriteriaBuilder.In<Object> status = criteriaBuilder.in(root.get("orderStatus"));
			status.value(5);
			status.value(8);
			status.value(10);
			status.value(15);
			CriteriaBuilder.In<Object> split = criteriaBuilder.in(root.get("splitFlag"));
			split.value(100);
			split.value(200);
			query.where(criteriaBuilder.and(sid, status, split));
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
	public Page<Orders> findOrderListByShopId(Long shopId, Integer status, Integer page, Integer size) {
//      传入分页和排序属性
		PageRequest pageable = PageRequest.of(page, size, Sort.Direction.DESC, "orderTime");
//      查找orders表
		Page<Orders> ordersPage = ordersDao.findAll((root, query, criteriaBuilder) -> {
			Predicate p0 = criteriaBuilder.equal(root.get("isDelete").as(Integer.class), 1);
			Predicate p1 = criteriaBuilder.equal(root.get("shopId").as(Long.class), shopId);
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
    public Object findRankingList(Integer start, Integer end) {
        Set<TypedTuple<Object>> reverseRange = redisUtil.reverseRange(RedisConstant.REDIS_PRODUCT_RANKING_LIST, start, end);
        List<Ranking> rankingList = new ArrayList<>();
        for (TypedTuple<Object> typedTuple : reverseRange) {
            Ranking ranking = new Ranking();
            ranking.setProductId(Long.parseLong(typedTuple.getValue().toString()));
            ranking.setQuantity(typedTuple.getScore());
            rankingList.add(ranking);
        }
        return new RankingListVO(rankingList);
    }

}
