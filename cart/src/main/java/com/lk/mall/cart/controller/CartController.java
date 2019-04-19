package com.lk.mall.cart.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.cart.model.Product;
import com.lk.mall.cart.model.Shop;

@RestController
public class CartController {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @RequestMapping("/findCartByUserId")
    public String findCartByUserId(@RequestParam("userId") String userId) {
        String value = stringRedisTemplate.opsForValue().get(userId);
        return value;
    }
    
    @RequestMapping("/addCart")
    public String addCart(
            @RequestParam("userId") Long userId, 
            @RequestParam("shopId") Long shopId,
            @RequestParam("productId") Long productId) {
        
//        String cartList = stringRedisTemplate.opsForValue().get(userId.toString());
        
        List<Product> productList = new ArrayList<>();
        productList.add(new Product(1L,2));
        Shop shop = new Shop(1L, productList);
        List<Shop> shopList = new ArrayList<>();
        shopList.add(shop);
        
//        stringRedisTemplate.opsForValue().set(userId.toString(), userId);
        return userId.toString();
    }

}
