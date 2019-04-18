package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Embedded;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SettlementVO {
	private BigDecimal orderMoney;
	private BigDecimal shippingFee;
	@Embedded
	private List<ShopVO> shopList;
	
	public BigDecimal getShopAllMoney() {
		BigDecimal b = BigDecimal.ZERO;
		for (ShopVO shopVO : shopList) {
			b = b.add(shopVO.getShopMoney());
		}
		return b;
	}
	
}
