package com.lk.mall.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class Cart implements Serializable, Cloneable {

	private static final long serialVersionUID = -5510967080302695103L;

	private Boolean check;
	private List<ShopCart> shopList;
	@JSONField(serialize = false)
	private Boolean isAll;
	@JSONField(serialize = false)
	private Integer shopListSize;
	@JSONField(serialize = false)
	private BigDecimal orderMoney;
	
	public Cart(List<ShopCart> shopList, Boolean isAll, Boolean check) {
	    super();
	    this.shopList = shopList;
	    this.isAll = isAll;
	    this.check = check;
	}
	
    
    public Integer getShopListSize() {
        return shopList == null ? 0 : shopList.size();
    }
    
    
    public BigDecimal getOrderMoney() {
        return shopList.stream().map(ShopCart::getShopMoney).reduce(BigDecimal.ZERO, BigDecimal::add);
    }
	
}
