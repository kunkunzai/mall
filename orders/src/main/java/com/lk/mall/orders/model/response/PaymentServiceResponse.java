package com.lk.mall.orders.model.response;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentServiceResponse {
	private Integer type;
	private String orderId;
	private LocalDateTime payTime;;
	private BigDecimal orderMoney;
	private String tradeId;
}

