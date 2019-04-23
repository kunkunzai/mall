package com.lk.mall.cart.service;

import com.lk.mall.cart.model.Cart;
import com.lk.mall.cart.model.Check;
import com.lk.mall.cart.model.ShopCart;

public interface ICartService {
    
    /**
     * 加入购物车
     * @param shopCart
     * @param userId
     * @return
     */
	Integer addCart(ShopCart shopCart, String userId);

    /**
     * 清空购物车
     * @param userId
     * @return
     */
    Integer deleteCart(String userId, Cart cart);
    
    /**
     * 获得用户购物车信息
     * @param userId
     * @return
     */
    Cart getCartList(String userId, Boolean isPopulate);
    
    /**
     * 获得某一用户购物车角标
     * @param userId
     * @return
     */
    Integer getCartMark(String userId);
    
    /**
     * 选中&取消选中
     * @param cart
     * @return
     */
    Integer checkCart(Check check, String userId);
    
}