package com.fpoly.thainv.tholh.service;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.stereotype.Service;

import com.fpoly.thainv.entities.CartProduct;
import com.fpoly.thainv.entities.ShoppingCarts;
import com.fpoly.thainv.tholh.JPA.CartJPA;
import com.fpoly.thainv.tholh.JPA.CartProductJPA;
import com.fpoly.thainv.tholh.JPA.CouponJPA;

import jakarta.servlet.http.HttpSession;

@Service
public class CartService {

    @Autowired
    CartJPA cartJPA;

    @Autowired
    CartProductJPA cartProductJPA;

    @Autowired
    HttpSession session;

    public Set<CartProduct> getCartList() {
        ShoppingCarts shoppingCart = (ShoppingCarts) session.getAttribute("cart");
        if(shoppingCart != null){
            Set<CartProduct> cartProducts = shoppingCart.getCartProducts();
            return (shoppingCart == null || cartProducts == null) ? (new HashSet<CartProduct>(0)) : cartProducts;
        }

        return (new HashSet<CartProduct>(0));
    }

    public void update(int id, int quantity) {
        Set<CartProduct> cartProducts = this.getCartList();
        for (CartProduct cartProduct : cartProducts) {
            if (cartProduct.getCartPrdId() == id) {
                cartProduct.setQuantity(quantity);

                cartProductJPA.save(cartProduct);
                break;
            }
        }

    }

    public int getCount() {
        int count = 0;

        Set<CartProduct> cartProducts = this.getCartList();
        for (CartProduct cart : cartProducts) {
            int quantity = cart.getQuantity();
            count += quantity;
        }

        return count;
    }

    public int getAmount(int id) {
        int amount = 0;

        Set<CartProduct> cartProducts = this.getCartList();
        for (CartProduct cart : cartProducts) {
            if (cart.getCartPrdId() == id) {
                int price = cart.getProducts().getRetailPrice().intValue();
                amount = cart.getQuantity() * price;
            }
        }

        return amount;
    }

    public int getTotal(int id) {
        int total = 0;
    
        Set<CartProduct> cartProducts = this.getCartList();
        for (CartProduct cart : cartProducts) {
            if (cart.getShoppingCarts().getCartId() == id) {
                int price = cart.getProducts().getRetailPrice().intValue();
                int amount = cart.getQuantity() * price;
                total += amount;
            }
        }
    
        return total;
    }
    
}
