package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductVO{
	private Long id;
	private String orderId;
	private Long productId;
	private Integer quantity;
	private BigDecimal productMoney;
	private BigDecimal totalMoney;
	private BigDecimal refundAmount;
	private String productName;
	private String productSubtitle;
	private String productImage;
	private Integer productType;
	private String orderItemsDesc;
	
	public BigDecimal getItemAllMoney() {
		return productMoney.multiply(new BigDecimal(quantity));
	}
	
}
