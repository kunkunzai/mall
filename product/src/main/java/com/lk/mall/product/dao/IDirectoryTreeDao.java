package com.lk.mall.product.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.lk.mall.product.model.DirectoryTree;

public interface IDirectoryTreeDao extends JpaRepository<DirectoryTree, Long>, JpaSpecificationExecutor<DirectoryTree> {
	
}
