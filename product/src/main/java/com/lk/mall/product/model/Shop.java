package com.lk.mall.product.model;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "shop")
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class Shop {
	@Id
	@Column(name = "id", unique = true, nullable = false)
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@NotNull(message="店铺类型不能为空")
	private Integer type;
	@NotEmpty(message="店铺名称不能为空")
	private String name;
	private String logo;
	private String description;
	private Integer status;
	private Timestamp create_time;
	private Timestamp update_time;
}
