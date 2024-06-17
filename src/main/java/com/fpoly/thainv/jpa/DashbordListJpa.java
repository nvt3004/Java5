package com.fpoly.thainv.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.fpoly.thainv.entities.Orders;
import com.fpoly.thainv.models.DashbordList;

@Repository
public interface DashbordListJpa extends JpaRepository<Orders, Integer> {

    @Query("""
                WITH OrderSummary AS (
                    SELECT
                        o.orderDate AS orderDate,
                        SUM(o.totalAmount) AS totalTotalAmount,
                        SUM(od.quantity * p.importPrice) AS totalImportPrice,
                        SUM(od.quantity * p.retailPrice) AS totalRetailPrice,
                        SUM(od.quantity * p.wholesalePrice) AS totalWholesalePrice,
                        SUM(o.totalAmount - od.quantity * p.importPrice) AS totalRevenue
                    FROM
                        Orders o
                    JOIN
                        OrderDetails od ON o.orderId = od.orders.orderId
                    JOIN
                        Products p ON od.products.productId = p.productId
                    GROUP BY
                        o.orderDate
                )
                SELECT
                    ROW_NUMBER() OVER (ORDER BY orderDate) AS id,
                    ROW_NUMBER() OVER (ORDER BY orderDate) AS id2,
                    os.orderDate AS orderDate,
                    os.totalTotalAmount AS totalTotalAmount,
                    os.totalImportPrice AS totalImportPrice,
                    os.totalRetailPrice AS totalRetailPrice,
                    os.totalWholesalePrice AS totalWholesalePrice,
                    os.totalRevenue AS totalRevenue
                FROM
                    OrderSummary os
            """)
    List<Object[]> getDashboardData();

    @Query(value = """
            WITH OrderSummary AS (
                SELECT
                    DATEADD(month, DATEDIFF(month, 0, o.order_date), 0) AS orderMonth,
                    SUM(o.total_amount) AS totalTotalAmount,
                    SUM(od.quantity * p.import_price) AS totalImportPrice,
                    SUM(od.quantity * p.retail_price) AS totalRetailPrice,
                    SUM(od.quantity * p.wholesale_price) AS totalWholesalePrice,
                    SUM(o.total_amount - (od.quantity * p.import_price)) AS totalRevenue
                FROM
                    Orders o
                JOIN
                    order_details od ON o.order_id = od.order_id
                JOIN
                    products p ON od.product_id = p.product_id
                GROUP BY
                    DATEADD(month, DATEDIFF(month, 0, o.order_date), 0)
            )
            SELECT
                ROW_NUMBER() OVER (ORDER BY os.orderMonth) AS id,
                ROW_NUMBER() OVER (ORDER BY os.orderMonth) AS id2,
                CAST(os.orderMonth AS DATE) AS orderDate,
                os.totalTotalAmount AS totalTotalAmount,
                os.totalImportPrice AS totalImportPrice,
                os.totalRetailPrice AS totalRetailPrice,
                os.totalWholesalePrice AS totalWholesalePrice,
                os.totalRevenue AS totalRevenue
            FROM
                OrderSummary os
            """, nativeQuery = true)
    List<Object[]> getDashboardDataByMonth();

    // @Query(value = """
    //         WITH OrderSummary AS (
    //             SELECT
    //                 YEAR(o.order_date) AS orderYear,
    //                 SUM(o.totalAmount) AS totalTotalAmount,
    //                 SUM(od.quantity * p.importPrice) AS totalImportPrice,
    //                 SUM(od.quantity * p.retailPrice) AS totalRetailPrice,
    //                 SUM(od.quantity * p.wholesalePrice) AS totalWholesalePrice,
    //                 SUM(o.totalAmount - od.quantity * p.importPrice) AS totalRevenue
    //             FROM
    //                 Orders o
    //             JOIN
    //                 order_details od ON o.order_id = od.order_id
    //             JOIN
    //                 products p ON od.product_id = p.product_id
    //             GROUP BY
    //                 YEAR(o.order_date)
    //         )
    //         SELECT
    //             ROW_NUMBER() OVER (ORDER BY os.orderYear) AS id,
    //             ROW_NUMBER() OVER (ORDER BY os.orderYear) AS id2,
    //             CAST(os.orderYear AS DATE) AS orderYear,
    //             os.totalTotalAmount AS totalTotalAmount,
    //             os.totalImportPrice AS totalImportPrice,
    //             os.totalRetailPrice AS totalRetailPrice,
    //             os.totalWholesalePrice AS totalWholesalePrice,
    //             os.totalRevenue AS totalRevenue
    //         FROM
    //             OrderSummary os
    //         """, nativeQuery = true)
    // List<Object[]> getDashboardDataByYear();

}
