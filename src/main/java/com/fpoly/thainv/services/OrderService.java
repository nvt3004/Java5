package com.fpoly.thainv.services;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.fpoly.thainv.entities.AttributeProduct;
import com.fpoly.thainv.entities.Attributes;
import com.fpoly.thainv.entities.OrderDetails;
import com.fpoly.thainv.entities.OrderStatus;
import com.fpoly.thainv.entities.Orders;
import com.fpoly.thainv.entities.Products;
import com.fpoly.thainv.jpa.OrderDetailJpa;
import com.fpoly.thainv.jpa.OrderJpa;
import com.fpoly.thainv.jpa.OrderStatusJpa;

@Service
public class OrderService {
    private final OrderDetailJpa orderDetailJpa;
    
    private final OrderJpa orderJpa;
    
    private final OrderStatusJpa orderStatusJpa;

    @Autowired
    public OrderService(OrderDetailJpa orderDetailJpa, OrderJpa orderJpa, OrderStatusJpa orderStatusJpa) {
        this.orderDetailJpa = orderDetailJpa;
		this.orderJpa = orderJpa;
		this.orderStatusJpa = orderStatusJpa;
    }


    public OrderDetails getOrderDetails(Integer orderId) {
        Optional<OrderDetails> orderDetailsOptional = orderDetailJpa.findById(orderId);
        return orderDetailsOptional.orElse(null);
    }

    public List<Attributes> getSizesForProduct(Products product) {
        Set<AttributeProduct> attributeProducts = product.getAttributeProducts();
        return filterAttributesByType(attributeProducts, "Size");
    }

    private List<Attributes> filterAttributesByType(Set<AttributeProduct> attributeProducts, String type) {
        return attributeProducts.stream()
                .map(AttributeProduct::getAttributes)
                .filter(attribute -> type.equals(attribute.getAttributeKey()))
                .collect(Collectors.toList());
    }
    
    public List<Attributes> getColorsForProduct(Products product) {
        Set<AttributeProduct> attributeProducts = product.getAttributeProducts();
        return filterAttributesByType(attributeProducts, "Color");
    }
    
    public List<Products> getProductsByOrderId(Integer orderId) {
        List<Products> products = orderDetailJpa.findProductsByOrderId(orderId);
        return products;
    }

    public List<OrderDetails> findProductsByOrderId(Integer orderId) {
        List<OrderDetails> orderDetailsList = orderDetailJpa.findByOrdersOrderId(orderId);
        return orderDetailsList;
    }

    public boolean updateOrderStatusToCancelled(String orderId) {
        Optional<Orders> orderOptional = orderJpa.findById(orderId);
        if (!orderOptional.isPresent()) {
            return false;
        }
        Orders order = orderOptional.get();

        Integer cancelledStatusId = 5;
        OrderStatus cancelledStatus = orderStatusJpa.findById(String.valueOf(cancelledStatusId)).orElse(null);
        if (cancelledStatus == null) {
            return false;
        }

        order.setOrderStatus(cancelledStatus);
        orderJpa.save(order);
        return true;
    }
    
    public boolean restoreOrderStatus(String orderId) {
        Optional<Orders> orderOptional = orderJpa.findById(orderId);
        if (!orderOptional.isPresent()) {
            return false;
        }
        Orders order = orderOptional.get();

        Integer cancelledStatusId = 1;
        OrderStatus cancelledStatus = orderStatusJpa.findById(String.valueOf(cancelledStatusId)).orElse(null);
        if (cancelledStatus == null) {
            return false;
        }

        order.setOrderStatus(cancelledStatus);
        orderJpa.save(order);
        return true;
    }
    
    public Page<Orders> getOrders(String name, String address, String status, int entries, int page) {
        Pageable pageable = PageRequest.of(page, entries, Sort.by("orderDate").descending());
        return orderJpa.findOrdersByCriteria(name, address, status, pageable);
    }
}


