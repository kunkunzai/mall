package com.lk.mall.cart.service.impl;

import java.math.BigDecimal;
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
import com.lk.mall.cart.model.ProductCart;
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
        if (null != cart && null != cart.getShopList()) {
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
//            填充商品信息
        	List<ProductServiceResponse> productServiceResponseList = getProductServiceResponse(cart);
        	BigDecimal orderMoney = BigDecimal.ZERO;
            for (ShopCart shopCart : cart.getShopList()) {
                BigDecimal shopMoney = BigDecimal.ZERO;
                for (ProductCart productCart : shopCart.getProductList()) {
                    for (ProductServiceResponse productServiceResponse : productServiceResponseList) {
                        if (productCart.getProductId() == productServiceResponse.getId()) {
                            productCart.setProductImage(productServiceResponse.getImage());
                            productCart.setProductMoney(productServiceResponse.getPrice());
                            productCart.setProductName(productServiceResponse.getName());
                            productCart.setSubtitle(productServiceResponse.getDescription());
                            productCart.setStatus(productServiceResponse.getStatus());
                            productCart.setStock(productServiceResponse.getStock());
                            if (1 == productServiceResponse.getStatus()) {
                                productCart.setTotalMoney(productCart.getProductMoney().multiply(new BigDecimal(productCart.getQuantity())));
                            }else {
                                productCart.setTotalMoney(BigDecimal.ZERO);
                            }
                            shopCart.setShopName(productServiceResponse.getShopName());
                            shopMoney = shopMoney.add(productCart.getTotalMoney());
                        }
                    }
                }
                shopCart.setShopMoney(shopMoney);
                orderMoney = orderMoney.add(shopMoney);
            }
            cart.setOrderMoney(orderMoney);
//        	  定义个list用来存放失效的商品
            List<ProductCart> productList = new ArrayList<>();
//            过滤一遍购物车,将过滤到失效的商品,将它放到用来存放失效的商品的list中,并且从原list中移除
//            将list从大到小循环可以保证删除不报错
            for (int i = cart.getShopList().size() - 1; i >= 0; i--) {
                ShopCart shopCart = cart.getShopList().get(i);
                for (int j = shopCart.getProductList().size() - 1; j >= 0; j--) {
                    ProductCart productCart = shopCart.getProductList().get(j);
					if (null == productCart.getStatus() || 2 == productCart.getStatus()) {
//                        过滤到失效的商品,将它放到用来存放失效的商品的list中,并且从原list中移除
                        productList.add(productCart);
                        shopCart.getProductList().remove(j);
                    }
                }
//                如果一个商家的所有商品都失效了,那么将这个商家移除
                if (shopCart.getProductList().size() == 0) {
                    cart.getShopList().remove(i);
                }
            }
            cart.setLoseEfficacyList(productList);
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
		List<ProductServiceResponse> productList = productService.findGoodsDetail(list);
		Optional.ofNullable(productList).orElseThrow(() -> new ServicesNotConnectedException());
		return productList;
	}

}