package com.lk.mall.orders;

import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.lk.mall.orders.OrdersApplication;
import com.lk.mall.orders.service.IUserDisposeService;

@RunWith(SpringRunner.class) // 等价于使用 @RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = { OrdersApplication.class})
public class UserDisposeTest {
    
    @Autowired
    IUserDisposeService userDisposeService;
    
    @Test
    public void freezeOrder() {
        ExecutorService newFixedThreadPool = Executors.newFixedThreadPool(10);
        Future<?> future = newFixedThreadPool.submit(() -> {
            for (int i = 0; i < 10; i++) {
                System.out.println(userDisposeService.freezeOrder(String.valueOf(new Random().nextInt(10)), "1", "1"));
            }
        });
        try {
            future.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
    
}
