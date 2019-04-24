package com.lk.mall.orders.model.vo;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Embedded;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class OrdersVO {
	private Long id;
	private String parentOrderId;
	private String orderId;
	private Integer orderWay;
	private Integer orderStatus;
	private String orderDesc;
	private Long shopId;
	private Long userId;
	private String receiver;
	private String mobile;
//	private LocalDateTime orderTime;
	private LocalDateTime payTime;;
	private BigDecimal orderMoney;
	private BigDecimal refundMoney;
	private BigDecimal shippingFee;
	private String tradeId;
	private String address;
	private String zipCode;
	private Integer splitFlag;
	private LocalDateTime proCycleTime;
//	private LocalDateTime proCycleTimeE;
	private Timestamp userReceiveTime;
	private String logisticsCompany;
	private String logisticsCompanyCode;
	private String logisticsNo;
	@Embedded
	private List<ShopVO> shopList;
	
}
