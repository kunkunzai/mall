package com.lk.mall.product.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.product.dao.IOptionDao;
import com.lk.mall.product.model.PropOption;
import com.lk.mall.product.service.IOptionService;

@Service
public class OptionServiceImpl implements IOptionService{
    
    @Autowired
    private IOptionDao optionDao;

    @Override
    public List<PropOption> findByPropId(List<Long> propIds) {
        List<PropOption> optionList = optionDao.findAllById(propIds);
        return optionList;
    }

    

}
