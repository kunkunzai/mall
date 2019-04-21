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
public class Cart implements Serializable, Cloneable {

	private static final long serialVersionUID = -5510967080302695103L;

	private List<ShopCart> shopList;
	private Boolean isAll;
	
	public Cart(List<ShopCart> shopList, Boolean isAll) {
	    super();
	    this.shopList = shopList;
	    this.isAll = isAll;
	}
	
    
    public Integer getShopListSize() {
        return shopList == null ? 0 : shopList.size();
    }
    
    
    public BigDecimal getOrderMoney() {
        return shopList.stream().map(ShopCart::getShopMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
	
}
