package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.validation.constraints.NotEmpty;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentVO {
	@NotEmpty(message = "订单号不能为空")
	private String orderId;
	private LocalDateTime payTime;
	private BigDecimal orderMoney;
	@NotEmpty(message = "交易号不能为空")
	private String tradeId;
	
}
