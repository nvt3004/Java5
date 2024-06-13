package com.fpoly.thainv.tholh.JPA;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.CartProduct;

public interface CartProductJPA extends JpaRepository<CartProduct, String>{
    
}
