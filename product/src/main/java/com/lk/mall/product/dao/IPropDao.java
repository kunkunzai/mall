package com.lk.mall.product.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.product.model.Prop;

public interface IPropDao extends JpaRepository<Prop, Long>, JpaSpecificationExecutor<Prop> {
    
    List<Prop> findByDirectoryId(Long directoryId);
	
}
