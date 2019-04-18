package com.lk.mall.product.model.vo;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductVO {
	private Long id;
	private Long shopId;
	private Integer type;
	private String name;
	private String smallImage;
	private String model;
	private Integer status;
	private BigDecimal originalPrice;
	private BigDecimal salePrice;
	private String description;
	private String shopName;
	private Integer shopType;
}
