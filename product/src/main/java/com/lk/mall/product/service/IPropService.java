package com.lk.mall.product.service;

import java.util.List;

import com.lk.mall.product.model.Prop;

public interface IPropService {

    List<Prop> findByDirectoryTreeId(List<Long> directoryTreeIds);
	
}
