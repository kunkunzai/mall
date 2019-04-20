package com.lk.mall.cart.controller;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.cart.model.Cart;
import com.lk.mall.cart.model.ProductCart;
import com.lk.mall.cart.model.ShopCart;
import com.lk.mall.cart.service.ICartService;

@RestController
public class CartController {

    @Autowired
    private ICartService cartService;

    /**
     * 加入购物车
     * 
     * @param userId
     * @param shopId
     * @param productId
     * @return
     */
    @RequestMapping("/addCart")
    public Object addCart(
            @RequestParam("userId") String userId, 
            @RequestParam("shopId") Long shopId,
            @RequestParam("productId") Long productId) {

        ShopCart shopCart = new ShopCart(shopId, Arrays.asList(new ProductCart(productId, 1)), false);
        cartService.addCart(shopCart, userId.toString());
        return 200;
    }

    /**
     * 删除购物车 将该商品从购物车里删除
     * 
     * @param request
     * @return
     */
    @RequestMapping("/deleteCart")
    public Object deleteCart(@RequestBody Cart cart, @RequestParam("userId") String userId) {
        cartService.deleteCart(userId, cart);
        return 200;
    }

    /**
     * 得到购物车角标
     * 
     * @param request
     * @return
     */
    @RequestMapping("/getCartMark")
    public Object getCartMark(@RequestParam("userId") String userId) {
        int mark = cartService.getCartMark(userId);
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
        Cart cart = cartService.getCartList(userId);
        return cart;
    }
}