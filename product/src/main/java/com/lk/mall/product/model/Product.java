package com.lk.mall.product.model;

import java.math.BigDecimal;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "item")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Product {
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	private Long shopId;
	private Integer type;
	private String name;
	private String smallImage;
	private String model;
	private Integer status;
	private BigDecimal originalPrice;
	private BigDecimal salePrice;
	private String description;
	private String currentItem;
	private String relevantItem;
	private Timestamp createTime;
	private Timestamp updateTime;

}
