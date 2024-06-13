package com.fpoly.thainv.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.thainv.entities.Coupons;
import com.fpoly.thainv.jpa.CouponJpa;

@Service
public class CouponService {
    @Autowired
    private CouponJpa couponJpa;
    
    public Optional<Coupons> validateAndRetrieveCoupon(String code) {
        Optional<Coupons> couponOpt = couponJpa.findByCouponCode(code);
        
        return couponOpt;
    }
}