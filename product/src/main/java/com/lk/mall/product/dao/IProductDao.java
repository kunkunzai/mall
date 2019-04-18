package com.lk.mall.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.product.model.Product;

public interface IProductDao extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
	
}
