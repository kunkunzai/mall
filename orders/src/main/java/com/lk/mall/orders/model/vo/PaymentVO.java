package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class PaymentVO {
	private String orderId;
	private LocalDateTime payTime;;
	private BigDecimal orderMoney;
	private String tradeId;
	
}
