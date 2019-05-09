package com.lk.mall.cart.model.response;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductServiceResponse {
	private Long id;
	private Long shopId;
	private Integer type;
	private String name;
	private String image;
	private String model;
	private Integer status;
	private BigDecimal price;
	private String description;
	private String shopName;
	private Integer stock;
}

