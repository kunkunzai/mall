package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Embedded;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShopVO {
	private Long shopId;
	private String shopName;
	private Integer shopType;
	private BigDecimal totalMoney;
	@Embedded
	private List<ProductVO> productList;
	
	public BigDecimal getShopMoney() {
		BigDecimal b = BigDecimal.ZERO;
		for (ProductVO itemVO : productList) {
			b = b.add(itemVO.getProductAllMoney());
		}
		return b;
	}
}
