package com.lk.mall.product.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonIgnore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "product")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Product {
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull(message="店铺id不能为空")
	private Long shopId;
	@NotNull(message="商品类型不能为空")
	private Integer type;
	@NotEmpty(message="商品名称不能为空")
	private String name;
	@Transient
	private String shopName;
	@NotEmpty(message="商品图片不能为空")
	private String smallImage;
	private String model;
	private Integer status;
	@NotNull(message="原始价不能为空")
	private BigDecimal originalPrice;
	@NotNull(message="促销价不能为空")
	private BigDecimal salePrice;
	private String description;
	@JsonIgnore
	private String currentItem;
	@JsonIgnore
	private String relevantItem;
	@JsonIgnore
	private LocalDateTime createTime;
	@JsonIgnore
	private LocalDateTime updateTime;

}
