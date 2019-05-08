package com.lk.mall.product.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.product.model.Product;

public interface IProductDao extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product> {
    
    List<Product> findByShopId(Long shopId);
    
    List<Product> findByDirectoryId(Long directoryId);
    
}
