package com.fpoly.thainv.tholh.JPA;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.OrderStatus;

public interface OrderStatusJPA extends JpaRepository<OrderStatus, String>{
    
}
