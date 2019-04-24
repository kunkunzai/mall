package com.lk.mall.cart.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.cart.model.Cart;
import com.lk.mall.cart.service.ICartQueryService;

@RestController
public class CartQueryController {

    @Autowired
    private ICartQueryService cartQueryService;

    /**
     * 得到购物车角标
     * 
     * @param request
     * @return
     */
    @RequestMapping("/getCartMark")
    public Object getCartMark(@RequestParam("userId") String userId) {
        int mark = cartQueryService.getCartMark(userId);
        return mark;
    }

    /**
     * 查看购物车列表
     * 
     * @param request
     * @return
     */
    @RequestMapping("/getCartList")
    public Object getCartList(@RequestParam("userId") String userId) {
        Cart cart = cartQueryService.getCartList(userId, true);
        return cart;
    }
}