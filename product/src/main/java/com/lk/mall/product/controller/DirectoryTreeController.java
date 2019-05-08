package com.lk.mall.product.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lk.mall.product.model.DirectoryTree;
import com.lk.mall.product.service.IDirectoryTreeService;


@RestController
public class DirectoryTreeController {
    
    @Autowired
    private IDirectoryTreeService directoryTreeService;
    
    @RequestMapping("/findProductPropAndOption")
    public DirectoryTree findProductPropAndOption(@RequestParam("directoryId") Long directoryId) {
        return directoryTreeService.findById(directoryId);
    }

}
