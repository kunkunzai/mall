package com.lk.mall.cart.controller;

import java.util.Arrays;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    	ShopCart shopCart = new ShopCart(shopId, Arrays.asList(new ProductCart(productId, 1, true)), true);
        cartDisposeService.addCart(shopCart, userId);
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
    
    @RequestMapping("/updateQuantity")
    public Object updateQuantity(            
    		@RequestParam("userId") String userId, 
            @RequestParam("shopId") Long shopId,
            @RequestParam("productId") Long productId,
            @RequestParam("quantity") Integer quantity) {
    	cartDisposeService.updateQuantity(shopId, productId, quantity, userId);
        return 200;
    }

    /**
     * 将该商品从购物车里删除
     * @param userId
     * @param productIdList
     * @return
     */
    @RequestMapping("/deleteProduct")
    public Object deleteProduct(@RequestParam("userId") String userId, @Valid @RequestParam List<Long> productIdList) {
    	cartDisposeService.deleteProduct(userId, productIdList);
        return 200;
    }

}