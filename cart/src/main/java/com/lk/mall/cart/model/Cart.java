package com.lk.mall.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class Cart implements Serializable, Cloneable {

	private static final long serialVersionUID = -5510967080302695103L;

	private List<ShopCart> shopList;
	@JsonIgnore
	private Boolean isAll;
	
	public Cart(List<ShopCart> shopList, Boolean isAll) {
	    super();
	    this.shopList = shopList;
	    this.isAll = isAll;
	}
	
    
    public Integer getShopListSize() {
        return shopList == null ? 0 : shopList.size();
    }
    
    
    public BigDecimal getTotalMoney() {
        BigDecimal b = BigDecimal.ZERO;
        for (ShopCart shopCart : shopList) {
            b = b.add(shopCart.getShopMoney());
        }
        return b;
    }
	
}
