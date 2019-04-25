package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class SettlementVO {
	private BigDecimal orderMoney;
	@Embedded
	@NotEmpty(message = "商家明细不能为空")
	@Valid
	private List<ShopVO> shopList;
	
}
