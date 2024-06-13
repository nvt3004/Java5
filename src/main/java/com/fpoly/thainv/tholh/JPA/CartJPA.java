package com.fpoly.thainv.tholh.JPA;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.thainv.entities.ShoppingCarts;

import io.lettuce.core.dynamic.annotation.Param;

public interface CartJPA extends JpaRepository<ShoppingCarts, String> {

    @Query(value = "select * from shopping_carts s where s.user_id =:uID", nativeQuery = true)
    public ShoppingCarts findByUserID(@RequestParam("uID") String uID);

    @Query("SELECT s FROM ShoppingCarts s WHERE s.users.email = :email")
    ShoppingCarts findByUserEmail(@Param("email") String email);

}
