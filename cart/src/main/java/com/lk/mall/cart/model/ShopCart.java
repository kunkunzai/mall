package com.lk.mall.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class ShopCart implements Serializable, Cloneable {

	private static final long serialVersionUID = -5510967080302695103L;

	private Long shopId;
	private String shopName;
	private List<ProductCart> productList;
	private Boolean isAll;
	
	
    public ShopCart(Long shopId, List<ProductCart> productList, Boolean isAll) {
        super();
        this.shopId = shopId;
        this.productList = productList;
        this.isAll = isAll;
    }
    
    public Integer getProductListSize() {
        return productList.size();
    }
    
    public BigDecimal getShopMoney() {
        BigDecimal shopMoney = BigDecimal.ZERO;
        for (ProductCart productCart : productList) {
            BigDecimal productMoney = BigDecimal.ZERO;
            if (null != productCart.getProductMoney()) {
                productMoney = productCart.getProductMoney();
            }
            shopMoney = shopMoney.add(productMoney.multiply(new BigDecimal(productCart.getQuantity().toString())));
        }
        return shopMoney;
    }
    
	
}
