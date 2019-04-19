package com.lk.mall.cart.model;

import java.util.List;

import javax.persistence.Embedded;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Shop {
	private Long shopId;
	private String shopName;
	@Embedded
	private List<Product> itemList;
	
	
    public Shop(Long shopId, List<Product> itemList) {
        super();
        this.shopId = shopId;
        this.itemList = itemList;
    }
	
}
