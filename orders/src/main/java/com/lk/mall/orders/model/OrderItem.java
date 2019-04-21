package com.lk.mall.orders.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "order_item")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class OrderItem{
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@JsonIgnore
	private Long id;
	@JsonIgnore
	private String orderId;
	private Long productId;
	private Integer quantity;
	private BigDecimal productMoney;
	private BigDecimal totalMoney;
	private BigDecimal refundAmount;
	private String productName;
	private String productSubtitle;
	private String productImage;
	private Integer productType;
	private Long shopId;
	private String shopName;
	private Integer shopType;
	private String orderItemsDesc;
	@JsonIgnore
	private LocalDateTime createTime;
	@JsonIgnore
	private LocalDateTime updateTime;
}
