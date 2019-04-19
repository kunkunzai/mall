package com.lk.mall.cart.model;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Product{
	private Long productId;
	private Integer quantity;
	private BigDecimal productMoney;
	private Integer mark;
	
	
    public Product(Long productId, Integer quantity) {
        super();
        this.productId = productId;
        this.quantity = quantity;
    }
	
	
	
}
