package com.lk.mall.product.service;

import java.util.List;

import com.lk.mall.product.model.PropOption;

public interface IOptionService {

    List<PropOption> findByPropId(List<Long> propIds);
	

}
