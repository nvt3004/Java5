package com.fpoly.thainv.jpa;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.OrderStatus;

public interface OrderStatusJpa extends JpaRepository<OrderStatus, String>{
	
}
