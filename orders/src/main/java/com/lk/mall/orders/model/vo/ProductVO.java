package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductVO{
	@NotNull(message="商品id不能为空")
	private Long productId;
	@NotNull(message="购买数量不能为空")
	@Min(value = 1)
	private Integer quantity;
	@DecimalMin(value = "0.01")
	private BigDecimal productMoney;
	private BigDecimal totalMoney;
	private String productName;
	private String productSubtitle;
	private String productImage;
	private Integer productType;
	private Integer stock;
	
}
