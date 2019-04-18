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
	private List<ProductVO> itemList;
	
	public BigDecimal getShopMoney() {
		BigDecimal b = BigDecimal.ZERO;
		for (ProductVO itemVO : itemList) {
			b = b.add(itemVO.getItemAllMoney());
		}
		return b;
	}
}
