package com.fpoly.thainv.jpa;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.fpoly.thainv.entities.Coupons;

public interface CouponJpa  extends JpaRepository<Coupons, Integer> {
    Optional<Coupons> findByCouponCode(String code);
}