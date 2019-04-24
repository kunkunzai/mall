package com.lk.mall.cart.service;

import com.lk.mall.cart.model.Cart;

public interface ICartQueryService {
    
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
    
}