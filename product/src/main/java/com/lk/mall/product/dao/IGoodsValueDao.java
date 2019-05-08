package com.lk.mall.product.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.product.model.GoodsValue;

public interface IGoodsValueDao extends JpaRepository<GoodsValue, Long>, JpaSpecificationExecutor<GoodsValue> {
    
    List<GoodsValue> findByGoodsId(List<Long> goodsIds);
    
}
