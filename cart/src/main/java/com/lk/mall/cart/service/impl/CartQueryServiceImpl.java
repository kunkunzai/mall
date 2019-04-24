package com.lk.mall.cart.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.lk.mall.cart.constant.RedisConstant;
import com.lk.mall.cart.exception.ServicesNotConnectedException;
import com.lk.mall.cart.feign.IProductService;
import com.lk.mall.cart.model.Cart;
import com.lk.mall.cart.model.ShopCart;
import com.lk.mall.cart.model.response.ProductServiceResponse;
import com.lk.mall.cart.service.ICartQueryService;
import com.lk.mall.cart.utils.RedisUtil;

@Service
public class CartQueryServiceImpl implements ICartQueryService {

    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    IProductService productService;
    
    @Override
    public Integer getCartMark(String userId) {
        Cart cart = getCartList(userId, false);
		int sum = 0;
		if (cart.getShopList() != null) {
			for (ShopCart shopCart : cart.getShopList()) {
				sum += shopCart.getProductListSize();
			}
		}
		return sum;
    }

    @Override
    public Cart getCartList(String userId, Boolean isPopulate) {
        String cartStr = redisUtil.get(RedisConstant.REDIS_CART_PREFIX + userId);
		if (null == cartStr || cartStr.isEmpty()) {
			return null;
		}
        Cart cart = JSONObject.parseObject(cartStr, Cart.class);
        if(isPopulate) {
        	List<ProductServiceResponse> productServiceResponse = getProductServiceResponse(cart);
        	cart.getShopList().forEach(x -> {
        		x.getProductList().forEach(y -> {
        			productServiceResponse.forEach(z -> {
        				if (y.getProductId() == z.getId()) {
        					y.setProductImage(z.getSmallImage());
        					y.setProductMoney(z.getSalePrice());
        					y.setProductName(z.getName());
        					y.setSubtitle(z.getDescription());
        					x.setShopName(z.getShopName());
        				}
        			});
        		});
        	});
        }
        return cart;
    }
    
    // 调用product服务得到product详情
	private List<ProductServiceResponse> getProductServiceResponse(Cart cart) {
		List<Long> list = new ArrayList<>();
		cart.getShopList().forEach(x -> {
			x.getProductList().forEach(y -> {
				list.add(y.getProductId());
			});
		});
		List<ProductServiceResponse> productList = productService.findAllByProductId(list);
		Optional.ofNullable(productList).orElseThrow(() -> new ServicesNotConnectedException());
		return productList;
	}

}