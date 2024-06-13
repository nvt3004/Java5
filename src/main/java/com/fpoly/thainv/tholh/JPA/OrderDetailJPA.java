package com.fpoly.thainv.tholh.JPA;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.OrderDetails;

public interface OrderDetailJPA extends JpaRepository<OrderDetails, String>{
    
}
