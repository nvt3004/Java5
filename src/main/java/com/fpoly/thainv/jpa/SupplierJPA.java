package com.fpoly.thainv.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.fpoly.thainv.entities.Suppliers;

public interface SupplierJPA extends JpaRepository<Suppliers, Integer>, JpaSpecificationExecutor<Suppliers> {

}

