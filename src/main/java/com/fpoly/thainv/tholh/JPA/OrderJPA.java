package com.fpoly.thainv.tholh.JPA;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.Orders;

public interface OrderJPA extends JpaRepository<Orders, String>{
    
}
