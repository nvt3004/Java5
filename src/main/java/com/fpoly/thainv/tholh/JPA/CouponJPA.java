package com.fpoly.thainv.tholh.JPA;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.thainv.entities.Coupons;

public interface CouponJPA extends JpaRepository<Coupons, String>{
    @Query(value ="select *from coupons c where c.coupon_code = :code ", nativeQuery = true)
    public Coupons findByCode(@RequestParam("code") String code);
}
