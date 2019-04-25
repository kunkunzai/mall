package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class ShopVO {
	
	@NotNull(message="店铺id不能为空")
	private Long shopId;
	private String shopName;
	private Integer shopType;
	private BigDecimal shopMoney;
	@Embedded
	@NotEmpty(message = "商品明细不能为空")
	@Valid
	private List<ProductVO> productList;
}
