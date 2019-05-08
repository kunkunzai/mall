package com.lk.mall.product.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.product.dao.IPropDao;
import com.lk.mall.product.model.Prop;
import com.lk.mall.product.service.IPropService;

@Service
public class PropServiceImpl implements IPropService{
    
    @Autowired
    private IPropDao propDao;

    @Override
    public List<Prop> findByDirectoryTreeId(List<Long> directoryTreeIds) {
        List<Prop> propList = propDao.findAllById(directoryTreeIds);
        return propList;
    }
    

}
