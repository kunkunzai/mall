package com.lk.mall.cart.controller;

import java.util.Arrays;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.cart.model.Cart;
import com.lk.mall.cart.model.Check;
import com.lk.mall.cart.model.ProductCart;
import com.lk.mall.cart.model.ShopCart;
import com.lk.mall.cart.service.ICartDisposeService;

@RestController
public class CartDisposeController {

    @Autowired
    private ICartDisposeService cartDisposeService;

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
            @RequestParam("userId") Long userId, 
            @RequestParam("shopId") Long shopId,
            @RequestParam("productId") Long productId) {

        ShopCart shopCart = new ShopCart(shopId, Arrays.asList(new ProductCart(productId, 1, true)), false, true);
        cartDisposeService.addCart(shopCart, userId.toString());
        return 200;
    }

    /**
     * 选中&取消选中商品
     * @param userId
     * @param check
     * @return
     */
    @RequestMapping("/checkCart")
    public Object checkCart(@RequestParam("userId") String userId, @Valid @RequestBody Check check) {
    	cartDisposeService.checkCart(check, userId);
        return 200;
    }

    /**
     * 删除购物车 将该商品从购物车里删除
     * 
     * @param request
     * @return
     */
    @RequestMapping("/deleteCart")
    public Object deleteCart(@Valid @RequestBody Cart cart, @RequestParam("userId") String userId) {
    	cartDisposeService.deleteCart(userId, cart);
        return 200;
    }

}