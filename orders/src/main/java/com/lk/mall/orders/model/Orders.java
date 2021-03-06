package com.lk.mall.orders.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "orders")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Orders {
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;
	@JsonIgnore
	private String parentOrderId;
	private String orderId;
	private Integer orderWay;
	private Integer orderStatus;
	private String orderDesc;
	private Long shopId;
	private Long userId;
	private String receiver;
	private String mobile;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime orderTime;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime payTime;;
	private BigDecimal orderMoney;
	private BigDecimal refundMoney;
	private BigDecimal shippingFee;
	private String tradeId;
	private String address;
	private String zipCode;
	private Integer splitFlag;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime proCycleTime;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime proCycleTimeEnd;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime userReceiveTime;
	private String logisticsCompany;
	private String logisticsCompanyCode;
	private String logisticsNo;
	private Integer isDelete;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime createTime;
	@DateTimeFormat(iso = ISO.DATE_TIME)
	private LocalDateTime updateTime;
	@Embedded
	private List<OrderItem> orderItemList;
	
	public Integer getOrderItemSize() {
		return orderItemList.size();
	}
	
}
