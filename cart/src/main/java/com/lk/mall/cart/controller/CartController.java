package com.lk.mall.cart.controller;

import java.util.Arrays;
import java.util.List;

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
            @RequestParam("userId") Long userId, 
            @RequestParam("shopId") Long shopId,
            @RequestParam("productId") Long productId) {

        ShopCart shopCart = new ShopCart(shopId, Arrays.asList(new ProductCart(productId, 1, true)), true);
        cartService.addCart(shopCart, userId.toString());
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
        Cart cart = cartService.getCartList(userId, true);
        return cart;
    }
   
    /**
     * 选中&取消选中
     * @param userId
     * @param check
     * @return
     */
    @RequestMapping("/checkCart")
    public Object checkCart(@RequestParam("userId") String userId, @Valid @RequestBody Check check) {
        cartService.checkCart(check, userId);
        return 200;
    }
    
    /**
     * 删除商品
     * @param userId
     * @param productIdList
     * @return
     */
    @RequestMapping("/deleteProduct")
    public Object deleteProduct(@RequestParam("userId") String userId, @Valid @RequestParam List<Long> productIdList) {
        cartService.deleteProduct(userId, productIdList);
        return 200;
    }
}