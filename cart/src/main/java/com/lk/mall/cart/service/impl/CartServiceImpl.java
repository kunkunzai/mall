package com.lk.mall.cart.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.lk.mall.cart.constant.RedisConstant;
import com.lk.mall.cart.exception.ServicesNotConnectedException;
import com.lk.mall.cart.feign.IProductService;
import com.lk.mall.cart.model.Cart;
import com.lk.mall.cart.model.ProductCart;
import com.lk.mall.cart.model.ShopCart;
import com.lk.mall.cart.model.response.ProductServiceResponse;
import com.lk.mall.cart.service.ICartService;
import com.lk.mall.cart.utils.RedisUtil;

@Service
public class CartServiceImpl implements ICartService {

    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    IProductService productService;

    @Override
    public Integer addCart(ShopCart newShopCart, String userId) {
        Cart cart = getCartList(userId, false);
        Map<String, Integer> location = getListLocation(cart, newShopCart);
        int status = location.get("status");
        int shopFlag = location.get("shopFlag");
        int productFlag = location.get("productFlag");
        int quantity = location.get("quantity");
        /**
         * 4种情况,分为4种处理 
         * status==0,购物车为空，只需要在列表首位新增一个新的ShopCart即可
         * status==1,购物车没有该商家任何商品，只需要在列表首位新增一个新的ShopCart即可
         * status==2,购物车有该商家,没有该商品，需要将该商家数据移动到所有商家的首位，然后将新的商品放在该商家所有商品的首位
         * status==3,购物车有该商家,有该商品，需要将该商家数据移动到所有商家的首位，然后将新的商品(其实已存在)移动到该商家所有商品的首位,并修改数量
         */
        if (status == 0) {
            System.out.println("购物车为空");
//          购物车为空
            cart = new Cart(Arrays.asList(newShopCart), null);
        } else if (status == 1) {
            System.out.println("购物车没有该商家");
//          没有该商家任何商品，只需要在列表首位新增一个新的ShopCart即可
            cart.getShopList().add(0, newShopCart);
        } else if (status == 2) {
            System.out.println("购物车有该商家,没有该商品");
//          将该商家数据移动到所有商家的首位
            comeFirst(cart.getShopList(), shopFlag);
//          将新的商品放在该商家所有商品的首位
            cart.getShopList().get(0).getProductList().add(0, newShopCart.getProductList().get(0));
        } else if (status == 3) {
            System.out.println("购物车有该商家,有该商品");
//          将该商家数据移动到所有商家的首位      
            comeFirst(cart.getShopList(), shopFlag);
//          将新的商品(其实已存在)移动到该商家所有商品的首位         
            comeFirst(cart.getShopList().get(0).getProductList(), productFlag);
//          修改数量
            cart.getShopList().get(0).getProductList().get(0).setQuantity(quantity + 1);
        }
        redisUtil.set(RedisConstant.REDIS_CART_PREFIX + userId, JSON.toJSON(cart).toString());
        return 0;
    }

    /**
     * 将集合某个位置的元素移动到首位,其他位置元素不变
     * 
     * @param list 将要操作的集合
     * @param flag 要移动的元素下标
     */
    private <T> void comeFirst(List<T> list, int flag) {
        T t = list.get(flag);
        list.remove(flag);
        list.add(0, t);
    }

    /**
     * 获得新商家(及包含的商品)在已有购物车列表所在的位置
     * 
     * @param oldCartList
     * @param newShopCart
     * @return
     */
    private Map<String, Integer> getListLocation(Cart cart, ShopCart newShopCart) {
//        status有0,1,2,3这4种情况
//        1.status=0,该用户的购物车为空,shopFlag,productFlag,quantity为默认值0
//        2.status=1,该用户的购物车不为空,不包含该商家,shopFlag,productFlag,quantity为默认值0
//        3.status=2,该用户的购物车不为空,包含该商家,但是不包含该商品,shopFlag为该商家下标,productFlag,quantity为默认值0
//        4.status=3,该用户的购物车不为空,包含该商家,包含该商品,shopFlag为该商家下标,productFlag为该商家的该商品下标,quantity为购物车列表该商品的数量
        int status = 0;
        int shopFlag = 0;
        int productFlag = 0;
        int quantity = 0;
		if (null == cart || null == cart.getShopList() || cart.getShopList().size() == 0) {
//            status=0,该用户的购物车为空,shopFlag,productFlag,quantity为默认值0
        } else {
//            status=1,该用户的购物车不为空,不包含该商家,shopFlag,productFlag,quantity为默认值0
            status = 1;
            for (int i = 0; i < cart.getShopList().size(); i++) {
                ShopCart shopCart = cart.getShopList().get(i);
                if (shopCart.getShopId() == newShopCart.getShopId()) {
//                    status=2,该用户的购物车不为空,包含该商家,但是不包含该商品,shopFlag为该商家下标,productFlag,quantity为默认值0
                    status = 2;
                    shopFlag = i;
                    List<ProductCart> productList = shopCart.getProductList();
                    for (int j = 0; j < productList.size(); j++) {
                        ProductCart productCart = productList.get(j);
                        if (newShopCart.getProductList().get(0).getProductId() == productCart.getProductId()) {
//                            status=3,该用户的购物车不为空,包含该商家,包含该商品,shopFlag为该商家下标,productFlag为该商家的该商品下标,quantity为购物车列表该商品的数量
                            status = 3;
                            productFlag = j;
                            quantity = productCart.getQuantity();
                        }
                    }
                }
            }
        }
        Map<String, Integer> resultMap = new HashMap<>();
        resultMap.put("status", status);
        resultMap.put("shopFlag", shopFlag);
        resultMap.put("productFlag", productFlag);
        resultMap.put("quantity", quantity);
        System.out.println(resultMap.toString());
        return resultMap;
    }

    @Override
    public Integer deleteCart(String userId, Cart cart) {
//        全部删除
        if (cart.getIsAll()) {
            redisUtil.set(userId, null);
        } else {
//            定义2个list,一个用来存要删除的商家,一个用来存要删除的商品
            List<Long> shopIds = new ArrayList<>();
            List<Long> productIds = new ArrayList<>();
            cart.getShopList().forEach(x -> {
                if (x.getIsAll()) {
                    shopIds.add(x.getShopId());
                } else {
                    x.getProductList().forEach(y -> {
                        productIds.add(y.getProductId());
                    });
                }
            });
//            从服务器拿到购物车列表
            Cart cartList = getCartList(userId, false);
//            删除商家
            Iterator<ShopCart> shopCart = cartList.getShopList().iterator();
            while (shopCart.hasNext()) {
                ShopCart x = shopCart.next();
                for (Long shopId : shopIds) {
                    if (x.getShopId() == shopId) {
                        shopCart.remove();
                    }
                }
            }
//            删除商品
            for (ShopCart x : cartList.getShopList()) {
                Iterator<ProductCart> productCart = x.getProductList().iterator();
                while (productCart.hasNext()) {
                    ProductCart y = productCart.next();
                    for (Long productId : productIds) {
                        if (y.getProductId() == productId) {
                            productCart.remove();
                        }
                    }
                }
            }
            //TODO:再检查一遍结构,防止前端传错数据导致shopCart里的productList为空
            System.out.println(cartList.toString());
            redisUtil.set(RedisConstant.REDIS_CART_PREFIX + userId, JSON.toJSON(cartList).toString());
        }
        return 0;
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

    @Override
    public Integer getCartMark(String userId) {
        Cart cart = getCartList(userId, false);
//        int mark = cart.getShopList().stream().mapToInt(ShopCart::getProductListSize).sum();
		int sum = 0;
		if (cart.getShopList() != null) {
			for (ShopCart shopCart : cart.getShopList()) {
				sum += shopCart.getProductListSize();
			}
		}
		return sum;
    }

}