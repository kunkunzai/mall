package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;

import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ProductVO{
	private Long id;
	private String orderId;
	@NotNull(message="商品id不能为空")
	private Long productId;
	@NotNull(message="购买数量不能为空")
	private Integer quantity;
	private BigDecimal productMoney;
	private BigDecimal totalMoney;
	private BigDecimal refundAmount;
	private String productName;
	private String productSubtitle;
	private String productImage;
	private Integer productType;
	private String orderItemsDesc;
	
	public BigDecimal getProductAllMoney() {
		return productMoney.multiply(new BigDecimal(quantity));
	}
	
}
