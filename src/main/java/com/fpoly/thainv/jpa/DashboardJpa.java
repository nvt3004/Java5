package com.fpoly.thainv.jpa;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.fpoly.thainv.entities.Orders;
import com.fpoly.thainv.models.Dashbord;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

@Repository
public interface DashboardJpa extends JpaRepository<Orders, Integer> {
    @Query("SELECT new com.fpoly.thainv.models.Dashbord(" +
            "i.imgUrl, " +
            "p.productName, " +
            "p.categories.categoryName, " +
            "o.orderDate, " +
            "CAST(od.unitPrice AS double), " +
            "os.statusName) " +
            "FROM Orders o " +
            "JOIN o.orderDetailses od " +
            "JOIN od.products p " +
            "JOIN p.imageses i " +
            "JOIN o.orderStatus os " +
            "ORDER BY o.orderDate DESC")
    List<Dashbord> findTop10ByOrderByOrderDateDesc();

    @Query("SELECT " +
            "FUNCTION('MONTH', o.orderDate) AS orderMonth, " +
            "FUNCTION('YEAR', o.orderDate) AS orderYear, " +
            "SUM(COALESCE(o.totalAmount, 0)) AS totalAmountPerMonth " +
            "FROM Orders o " +
            "WHERE FUNCTION('YEAR', o.orderDate) = FUNCTION('YEAR', CURRENT_DATE) " +
            "GROUP BY FUNCTION('MONTH', o.orderDate), FUNCTION('YEAR', o.orderDate) " +
            "ORDER BY orderYear, orderMonth")
    List<Object[]> getTotalAmountPerMonth();
}
