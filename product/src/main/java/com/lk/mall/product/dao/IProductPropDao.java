package com.lk.mall.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.product.model.ProductProp;

public interface IProductPropDao extends JpaRepository<ProductProp, Long>, JpaSpecificationExecutor<ProductProp> {
    
	
}
