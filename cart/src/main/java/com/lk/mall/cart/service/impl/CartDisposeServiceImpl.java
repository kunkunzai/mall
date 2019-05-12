package com.lk.mall.cart.service.impl;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import com.lk.mall.cart.constant.RedisConstant;
import com.lk.mall.cart.exception.CartSaturationException;
import com.lk.mall.cart.model.Cart;
import com.lk.mall.cart.model.Check;
import com.lk.mall.cart.model.ProductCart;
import com.lk.mall.cart.model.ShopCart;
import com.lk.mall.cart.service.ICartDisposeService;
import com.lk.mall.cart.service.ICartQueryService;
import com.lk.mall.cart.utils.RedisUtil;

@Service
@PropertySource({"classpath:system.properties"})
public class CartDisposeServiceImpl implements ICartDisposeService {

    @Autowired
    private RedisUtil redisUtil;
    
    @Autowired
    private ICartQueryService cartQueryService;
    
    @Value("${cart.product.quantity.limit}")
    private Integer quantityLimit;

    @Override
    public Integer addCart(ShopCart newShopCart, String userId) {
        Cart cart = cartQueryService.getCartList(userId, false);
        Map<String, Integer> location = getListLocation(cart, newShopCart);
        int status = location.get("status");
        int shopFlag = location.get("shopFlag");
        int productFlag = location.get("productFlag");
        int quantity = location.get("quantity");
//      当该用户的购物车里的商品数量达到最大值时,禁止继续加入
        if (status == 1 || status == 2) {
            int sum = 0;
            for (ShopCart shopCart : cart.getShopList()) {
                sum += shopCart.getProductListSize();
            }
            if (sum >= quantityLimit) {
                System.out.println("购物车已满");
                throw new CartSaturationException(quantityLimit);
            }
        }
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
            cart = new Cart(Arrays.asList(newShopCart), true);
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
	public Integer deleteProduct(String userId, List<Long> productIdList) {
		Cart cart = null;
//		与前端约定,只传来一个0为全部删除
		if (productIdList.get(0) != 0) {
			cart = cartQueryService.getCartList(userId, false);
//      	删除商品
			for (int i = cart.getShopList().size() - 1; i >= 0; i--) {
				ShopCart shopCart = cart.getShopList().get(i);
				for (int j = shopCart.getProductList().size() - 1; j >= 0; j--) {
					ProductCart productCart = shopCart.getProductList().get(j);
					for (Long productId : productIdList) {
						if (productId == productCart.getProductId()) {
//                    		过滤到失效的商品,从原list中移除
							shopCart.getProductList().remove(j);
						}
					}
				}
//            	如果一个商家的所有商品都失效了,那么将这个商家移除
				if (shopCart.getProductList().size() == 0) {
					cart.getShopList().remove(i);
				}
			}
//			如果所有商家都被移除了,那么购物车列表就为空
			if (cart.getShopList().size() == 0) {
				cart = null;
			}
		}
		if(null==cart) {
			redisUtil.delete(RedisConstant.REDIS_CART_PREFIX + userId);
		}else {
			redisUtil.set(RedisConstant.REDIS_CART_PREFIX + userId, JSON.toJSON(cart).toString());
		}
		return null;
	}
    
    @Override
    public Integer checkCart(Check check) {
        Cart cart = cartQueryService.getCartList(check.getUserId(), false);
        Boolean checkStatus = check.getCheckStatus();
        Boolean checkAll = check.getCheckAll();
        if (null != checkAll) {
//            对购物车进行全选&取消全选操作
            if (checkStatus) {
//                全选
                refreshCartCheck(cart, 1);
            } else {
//                取消全选
                refreshCartCheck(cart, 7);
            }
        } else {
//            没有对购物车进行全选&取消全选操作,下面进入对商家或者商品操作
            if (null != check.getShopId()) {
//                对商家进行操作
                cart.getShopList().forEach(x -> {
                    if (x.getShopId() == check.getShopId()) {
                        x.setCheck(checkStatus);
                    }
                });
                if (checkStatus) {
//                    对商家进行选中
                    refreshCartCheck(cart, 2);
                } else {
//                    对商家进行取消选中
                    refreshCartCheck(cart, 8);
                }
            } else {
//                对商品进行操作
                if (null != check.getProductId()) {
                    cart.getShopList().forEach(x -> {
                        x.getProductList().forEach(y -> {
                            if (check.getProductId() == y.getProductId()) {
                                y.setCheck(checkStatus);
                            }
                        });
                    });
                    if (checkStatus) {
//                        对商品进行选中
                        refreshCartCheck(cart, 3);
                    } else {
//                       对商品进行取消选中 
                        refreshCartCheck(cart, 5);
                    }
                }else {
                    System.err.println("参数错误");
                }
            }
        }
        redisUtil.set(RedisConstant.REDIS_CART_PREFIX + check.getUserId(), JSON.toJSON(cart).toString());
        return null;
    }
    
    
    /**
     * 加入购物车不需要触发这个方法
     * 移出购物车分2种情况:
     *  非全选:需要触发这个方法:3和4
     *  全选:不需要触发这个方法
     * 购物车列表选中一定要触发这个方法
     * 
     * @param cart
     */
    private void refreshCartCheck(Cart cart, Integer condition) {
//        购物车列表操作可能出现的共8种情况
//        未选中->选中
//        大->小
//        1.购物车变成全选状态,所有商家所有商品要被选中
//          2.某个商家选中状态,那个商家的所有商品都要被选中,可能触发操作4
//        小->大
//        3.某个商家的所有商品都被选中,那个商家要被选中,可能触发操作4
//          4.所有商家都被选中,整个购物车都要被选中

//        选中->未选中
//        小->大
//        5.某个商品取消选中,对应商家取消选中,一定触发操作6
//          6.某个商家取消选中,整个购物车取消选中
//        大->小
//        7.购物车取消全选,所有商家所有商品取消选中
//          8.某个商家置取消选中,那么那个商家的所有商品取消选中,一定触发操作6
        switch (condition) {
        case 1:
//            购物车变成全选状态,所有商家所有商品要被选中
            cart.setCheck(true);
            cart.getShopList().forEach(x -> {
                x.setCheck(true);
                x.getProductList().forEach(y -> {
                    y.setCheck(true);
                });
            });
            break;
        case 2:
//            某个商家选中状态,那个商家的所有商品都要被选中
            cart.getShopList().forEach(x -> {
                if (x.getCheck()) {
                    x.getProductList().forEach(y -> {
                        y.setCheck(true);
                    });
                }
            });
//            可能触发操作4
            if (cart.getShopList().stream().allMatch(x -> x.getCheck())) {
                cart.setCheck(true);
            }
            break;
        case 3:
//            某个商家的所有商品都被选中,那个商家要被选中
            cart.getShopList().forEach(x -> {
                if (x.getProductList().stream().allMatch(y -> y.getCheck())) {
                    x.setCheck(true);
                }
            });
//          可能触发操作4
            if (cart.getShopList().stream().allMatch(x -> x.getCheck())) {
                cart.setCheck(true);
            }
            break;
        case 4:
//            所有商家都被选中,整个购物车都要被选中
            if (cart.getShopList().stream().allMatch(x -> x.getCheck())) {
                cart.setCheck(true);
            }
            break;
        case 5:
//            某个商品取消选中,对应商家及整个购物车取消选中
            cart.getShopList().forEach(x -> {
                if (x.getProductList().stream().anyMatch(y -> !y.getCheck())) {
                    x.setCheck(false);
                }
            });
            cart.setCheck(false);
            break;
        case 6:
//            某个商家取消选中,整个购物车取消选中
            if (cart.getShopList().stream().anyMatch(x -> !x.getCheck())) {
                cart.setCheck(false);
            }
            break;
        case 7:
//           购物车取消全选,所有商家取消选中
            cart.setCheck(false);
            cart.getShopList().forEach(x -> {
                x.setCheck(false);
                x.getProductList().forEach(y -> {
                    y.setCheck(false);
                });
            });
            break;
        case 8:
//            某个商家置取消选中,那么那个商家的所有商品取消选中
            cart.getShopList().forEach(x -> {
                if (!x.getCheck()) {
                    x.getProductList().forEach(y -> {
                        y.setCheck(false);
                    });
                }
            });
            cart.setCheck(false);
            break;
        default:
            System.err.println("输入有误");
            break;
        }
    }

	@Override
	public Integer updateQuantity(Long shopId, Long productId, Integer quantity, String userId) {
		Cart cart = cartQueryService.getCartList(userId, false);
		cart.getShopList().forEach(x -> {
			if (x.getShopId() == shopId) {
				x.getProductList().forEach(y -> {
					if (y.getProductId() == productId) {
						y.setQuantity(y.getQuantity() + quantity);
					}
				});
			}
		});
		redisUtil.set(RedisConstant.REDIS_CART_PREFIX + userId, JSON.toJSON(cart).toString());
		return 200;
	}

}