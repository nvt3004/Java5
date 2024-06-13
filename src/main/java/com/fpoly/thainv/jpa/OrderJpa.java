package com.fpoly.thainv.jpa;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.fpoly.thainv.entities.Orders;
import com.fpoly.thainv.models.OrderProductImageDTO;

public interface OrderJpa extends JpaRepository<Orders, String> {
	@Query("SELECT o FROM Orders o ORDER BY o.orderDate DESC")
	List<Orders> findAllOrderByOrderDateDesc();

	@Query("SELECT new com.fpoly.thainv.models.OrderProductImageDTO(" + "o.orderId, " + "od.orderDetailId, "
			+ "p.productId, " + "p.productName, " + "CAST(od.unitPrice AS double), " + "od.quantity, " + "i.imgUrl) "
			+ "FROM Orders o " + "JOIN o.orderDetailses od " + "JOIN od.products p " + "JOIN p.imageses i "
			+ "WHERE o.users.userId = :userId")
	List<OrderProductImageDTO> findOrderProductImagesByUserId(@Param("userId") Integer userId);

	@Query("SELECT COUNT(o) FROM Orders o WHERE o.users.userId = :userId")
	Integer countTotalOrdersByUserId(@Param("userId") Integer userId);

	@Query("SELECT SUM(od.quantity * od.unitPrice) FROM Orders o JOIN o.orderDetailses od WHERE o.users.userId = :userId")
	Double sumTotalAmountSpentByUserId(@Param("userId") Integer userId);

	@Query("SELECT SUM(od.quantity) FROM Orders o JOIN o.orderDetailses od WHERE o.users.userId = :userId")
	Integer sumTotalProductsBoughtByUserId(@Param("userId") Integer userId);
	
	@Query("SELECT o FROM Orders o WHERE "
	        + "(:name IS NULL OR CONCAT(o.users.firstName, ' ', o.users.lastName) LIKE CONCAT('%', :name, '%')) AND "
	        + "(:address IS NULL OR o.addresses.addressLine1 LIKE CONCAT('%', :address, '%')) AND "
	        + "(:status IS NULL OR o.orderStatus.statusName LIKE CONCAT('%', :status, '%'))")
	Page<Orders> findOrdersByCriteria(
	        @Param("name") String name,
	        @Param("address") String address,
	        @Param("status") String status,
	        Pageable pageable);

	 @Query("SELECT o FROM Orders o WHERE "
	            + "(:name IS NULL OR CONCAT(o.users.firstName, ' ', o.users.lastName) LIKE CONCAT('%', :name, '%')) AND "
	            + "(:address IS NULL OR o.addresses.addressLine1 LIKE CONCAT('%', :address, '%')) AND "
	            + "(:status IS NULL OR o.orderStatus.statusName LIKE CONCAT('%', :status, '%'))")
	    List<Orders> findOrdersByCriteria(
	            @Param("name") String name,
	            @Param("address") String address,
	            @Param("status") String status);
}
