package com.lk.mall.orders;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.lk.mall.orders.OrdersApplication;
import com.lk.mall.orders.model.Orders;
import com.lk.mall.orders.service.IUserQueryService;

@RunWith(SpringRunner.class) // 等价于使用 @RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { OrdersApplication.class})
public class UserQueryTest {
    @Autowired
    IUserQueryService userQueryService;

    @Test
    public void findOrderByOrderId() {
        Orders orders = userQueryService.findOrderByOrderId("6b9808f8cdc44cbc9d4be1a7227a4fd8");
        System.err.println(orders.toString());
    }
    
}
