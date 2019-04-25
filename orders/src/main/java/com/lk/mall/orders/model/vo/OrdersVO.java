package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Embedded;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrdersVO {
    @Max(value = 5)
    @Min(value = 1)
	private Integer orderWay;
    @Size(max = 100)
	private String orderDesc;
	@NotNull(message="用户id不能为空")
	@Min(value = 1)
	private Long userId;
    @Size(max = 5, min = 2)
	@NotEmpty(message="收货人姓名不能为空")
	private String receiver;
	@NotEmpty(message="手机号不能为空")
	@Pattern(regexp = "1[3|4|5|7|8][0-9]\\d{8}")
	private String mobile;
	@NotNull(message="总金额不能为空")
	@DecimalMin(value = "0.01")
	private BigDecimal orderMoney;
	private BigDecimal shippingFee;
	@Size(max = 25, min = 5)
	@NotEmpty(message="地址不能为空")
	private String address;
	private String zipCode;
	@Embedded
	@NotEmpty(message="商家列表不能为空")
	@Valid
	private List<ShopVO> shopList;
	
}
