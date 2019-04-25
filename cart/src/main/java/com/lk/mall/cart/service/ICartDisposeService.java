package com.lk.mall.cart.service;

import java.util.List;

import com.lk.mall.cart.model.Check;
import com.lk.mall.cart.model.ShopCart;

public interface ICartDisposeService {
    
    /**
     * 加入购物车
     * @param shopCart
     * @param userId
     * @return
     */
	Integer addCart(ShopCart shopCart, String userId);
	
	/**
	 * 选中&取消选中
	 * @param cart
	 * @return
	 */
	Integer checkCart(Check check, String userId);
	
	/**
	 * 添加商品数量
	 * @param shopId
	 * @param productId
	 * @param quantity
	 * @param userId
	 * @return
	 */
	Integer updateQuantity(Long shopId, Long productId, Integer quantity, String userId);

    /**
     * 删除商品
     * @param productIds
     * @param userId
     * @return
     */
    Integer deleteProduct(String userId, List<Long> productIdList);
    
}