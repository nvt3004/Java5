package com.fpoly.thainv.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpoly.thainv.entities.OrderDetails;
import com.fpoly.thainv.entities.Products;
import com.fpoly.thainv.models.Product;

public interface OrderDetailJpa extends JpaRepository<OrderDetails, Integer> {
	@Query("SELECT od.products FROM OrderDetails od WHERE od.orders.orderId = :orderId")
	List<Products> findProductsByOrderId(@Param("orderId") Integer orderId);

	List<OrderDetails> findByOrdersOrderId(Integer orderId);

}
