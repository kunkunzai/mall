package com.lk.mall.product.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.product.model.Goods;

public interface IGoodsDao extends JpaRepository<Goods, Long>, JpaSpecificationExecutor<Goods> {
    
    List<Goods> findByProductId(Long productId);
    
}
