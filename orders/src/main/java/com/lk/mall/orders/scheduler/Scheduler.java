package com.lk.mall.orders.scheduler;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.lk.mall.orders.dao.IOrdersDao;

@Component
public class Scheduler {
    
    @Autowired
    private IOrdersDao ordersDao;
    
//    @Scheduled(cron="0 0/1 * * * ?") //每分钟执行一次
    @Transactional
    public void cancelOrder() { 
        System.out.println("每分钟执行一次...");
        ordersDao.cancelOrder();
    }
    
    @Scheduled(cron="0 0 0 0/1 * ?") //每天执行一次
    @Transactional
    public void confirmDelivery() { 
        System.out.println("每天执行一次...");
        ordersDao.confirmDelivery();
    }  

}
