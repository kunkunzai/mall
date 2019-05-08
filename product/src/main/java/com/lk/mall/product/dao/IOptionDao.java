package com.lk.mall.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.product.model.PropOption;

public interface IOptionDao extends JpaRepository<PropOption, Long>, JpaSpecificationExecutor<PropOption> {
    
}
