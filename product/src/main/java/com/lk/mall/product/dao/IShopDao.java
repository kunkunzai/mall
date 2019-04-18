package com.lk.mall.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.product.model.Shop;

public interface IShopDao extends JpaRepository<Shop, Long>, JpaSpecificationExecutor<Shop> {
	
}
