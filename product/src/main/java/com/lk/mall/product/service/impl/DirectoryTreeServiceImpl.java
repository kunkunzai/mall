package com.lk.mall.product.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.lk.mall.product.dao.IDirectoryTreeDao;
import com.lk.mall.product.dao.IOptionDao;
import com.lk.mall.product.dao.IPropDao;
import com.lk.mall.product.model.DirectoryTree;
import com.lk.mall.product.model.Prop;
import com.lk.mall.product.model.PropOption;
import com.lk.mall.product.service.IDirectoryTreeService;

@Service
public class DirectoryTreeServiceImpl implements IDirectoryTreeService{
    
    @Autowired
    private IDirectoryTreeDao directoryTreeDao;
    @Autowired
    private IPropDao propDao;
    @Autowired
    private IOptionDao optionDao;

    @Override
    public DirectoryTree findById(Long id) {
        Optional<DirectoryTree> d = directoryTreeDao.findById(id);
        if (!d.isPresent()) {
            return null;
        }
        DirectoryTree directoryTree = d.get();
        List<Prop> propList = propDao.findByDirectoryId(id);
        List<Long> propIdList = propList.stream().map(Prop::getId).collect(Collectors.toList());
        List<PropOption> optionList = optionDao.findAll((root, query, criteriaBuilder) -> {
            CriteriaBuilder.In<Object> in = criteriaBuilder.in(root.get("propId"));
            propIdList.stream().forEach(x -> in.value(x));
            query.where(criteriaBuilder.and(in));
            return query.getRestriction();
        });
        for (Prop prop : propList) {
            List<PropOption> newOptionList = new ArrayList<>();
            for (PropOption option : optionList) {
                if (prop.getId() == option.getPropId()) {
                    newOptionList.add(option);
                }
            }
            prop.setOptionList(newOptionList);
        }
        directoryTree.setPropList(propList);
        return directoryTree;
    }

}
