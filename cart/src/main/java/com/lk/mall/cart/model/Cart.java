package com.lk.mall.cart.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.alibaba.fastjson.annotation.JSONField;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@Data
@ToString
public class Cart implements Serializable, Cloneable {

	private static final long serialVersionUID = -5510967080302695103L;

	@NotNull(message="选中状态不能为空")
	private Boolean check;
	private List<ShopCart> shopList;
	@JSONField(serialize = false)
	private Integer shopListSize;
	@JSONField(serialize = false)
	private BigDecimal orderMoney;
	private List<ProductCart> loseEfficacyList;
	
	public Cart(List<ShopCart> shopList, Boolean check) {
	    super();
	    this.shopList = shopList;
	    this.check = check;
	}
	
    
    public Integer getShopListSize() {
        return shopList == null ? 0 : shopList.size();
    }
	
}