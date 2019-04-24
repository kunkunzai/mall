package com.lk.mall.orders.service.impl;

import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.orders.dao.IOrdersDao;
import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.model.vo.ShipmentsVO;
import com.lk.mall.orders.service.IShopDisposeService;

@Service
public class ShopDisposeServiceImpl implements IShopDisposeService {

    @Autowired
    private IOrdersDao ordersDao;

    @Override
	public Integer shipments(ShipmentsVO shipmentsVO) {
		Orders orders = ordersDao.findByOrderId(shipmentsVO.getOrderId());
		if (null == orders) {
			System.out.println("订单不存在");
			return 100;
		}
		if (orders.getOrderStatus() != 8) {
			System.out.println("状态不可改变");
			return 100;
		}
		if (orders.getShopId() != shipmentsVO.getShopId()) {
			System.out.println("不是本人操作");
			return 100;
		}
		orders.setOrderStatus(10);
		orders.setLogisticsCompany(shipmentsVO.getLogisticsCompany());
		orders.setLogisticsCompanyCode(shipmentsVO.getLogisticsCompanyCode());
		orders.setLogisticsNo(shipmentsVO.getLogisticsNo());
		LocalDateTime now = LocalDateTime.now();
		orders.setProCycleTime(now);
		orders.setProCycleTimeEnd(now.plusDays(15));
		orders.setUpdateTime(now);
		ordersDao.saveAndFlush(orders);
		return 200;
	}

}
