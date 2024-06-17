package com.fpoly.thainv.services;

import java.math.BigDecimal;
import java.time.Year;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.thainv.jpa.DashboardJpa;
import com.fpoly.thainv.jpa.DashbordListJpa;
import com.fpoly.thainv.models.DashbordList;
import com.fpoly.thainv.models.MonthlyTotal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
public class DashboardService {

    private final DashboardJpa dashboardRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DashbordListJpa dashbordListJpa;

    @Autowired
    public DashboardService(DashboardJpa dashboardRepository) {
        this.dashboardRepository = dashboardRepository;
    }

    public Map<YearMonth, Integer> getTotalAmountPerMonth() {
        // Khởi tạo map để lưu trữ kết quả
        Map<YearMonth, Integer> monthlyTotals = new LinkedHashMap<>(); // Sử dụng LinkedHashMap để giữ thứ tự chèn

        // Truy vấn cơ sở dữ liệu để lấy tổng số tiền cho từng tháng trong năm hiện tại
        List<Object[]> results = entityManager.createQuery(
                "SELECT " +
                        "FUNCTION('MONTH', o.orderDate) AS orderMonth, " +
                        "FUNCTION('YEAR', o.orderDate) AS orderYear, " +
                        "SUM(COALESCE(o.totalAmount, 0)) AS totalAmountPerMonth " +
                        "FROM Orders o " +
                        "WHERE FUNCTION('YEAR', o.orderDate) = FUNCTION('YEAR', CURRENT_DATE) " +
                        "GROUP BY FUNCTION('MONTH', o.orderDate), FUNCTION('YEAR', o.orderDate) " +
                        "ORDER BY FUNCTION('YEAR', o.orderDate), FUNCTION('MONTH', o.orderDate)")
                .getResultList();

        // Đưa kết quả vào map monthlyTotals và đảm bảo rằng tất cả các tháng từ 1 đến
        // 12 đều có trong monthlyTotals
        int currentYear = Year.now().getValue();
        for (int month = 1; month <= 12; month++) {
            YearMonth yearMonth = YearMonth.of(currentYear, month);
            monthlyTotals.put(yearMonth, 0); // Mặc định giá trị là 0
        }

        // Xử lý kết quả từ truy vấn cơ sở dữ liệu
        for (Object[] result : results) {
            int month = (int) result[0];
            int year = (int) result[1];
            int totalAmount = ((Number) result[2]).intValue();

            YearMonth yearMonth = YearMonth.of(year, month);
            monthlyTotals.put(yearMonth, totalAmount);
        }

        return monthlyTotals;
    }

    public List<DashbordList> getDashboardData() {
        List<Object[]> rawResults = dashbordListJpa.getDashboardData();
        List<DashbordList> dashboardData = new ArrayList<>();

        for (Object[] result : rawResults) {
            DashbordList dashboard = new DashbordList();
            dashboard.setId(((Number) result[0]).intValue());
            dashboard.setId2(((Number) result[1]).intValue());
            dashboard.setOrder_date((Date) result[2]);
            dashboard.setTotal_total_amount(((Number) result[3]).doubleValue());
            dashboard.setTotal_import_price(((Number) result[4]).doubleValue());
            dashboard.setTotal_retail_price(((Number) result[5]).doubleValue());
            dashboard.setTotal_wholesale_price(((Number) result[6]).doubleValue());
            dashboard.setTotal_revenue(((Number) result[7]).doubleValue());

            dashboardData.add(dashboard);
        }

        return dashboardData;
    }


    public List<DashbordList> getDashboardDataByMonth() {
        List<Object[]> rawResults = dashbordListJpa.getDashboardDataByMonth();
        List<DashbordList> dashboardData = new ArrayList<>();

        for (Object[] result : rawResults) {
            DashbordList dashboard = new DashbordList();
            dashboard.setId(((Number) result[0]).intValue());
            dashboard.setId2(((Number) result[1]).intValue());
            dashboard.setOrder_date((Date) result[2]);
            dashboard.setTotal_total_amount(((Number) result[3]).doubleValue());
            dashboard.setTotal_import_price(((Number) result[4]).doubleValue());
            dashboard.setTotal_retail_price(((Number) result[5]).doubleValue());
            dashboard.setTotal_wholesale_price(((Number) result[6]).doubleValue());
            dashboard.setTotal_revenue(((Number) result[7]).doubleValue());

            dashboardData.add(dashboard);
        }

        return dashboardData;
    }

    // public List<DashbordList> getDashboardDataByYear() {
    //     List<Object[]> rawResults = dashbordListJpa.getDashboardDataByYear();
    //     List<DashbordList> dashboardData = new ArrayList<>();

    //     for (Object[] result : rawResults) {
    //         DashbordList dashboard = new DashbordList();
    //         dashboard.setId(((Number) result[0]).intValue());
    //         dashboard.setId2(((Number) result[1]).intValue());
    //         dashboard.setOrder_date((Date) result[2]);
    //         dashboard.setTotal_total_amount(((Number) result[3]).doubleValue());
    //         dashboard.setTotal_import_price(((Number) result[4]).doubleValue());
    //         dashboard.setTotal_retail_price(((Number) result[5]).doubleValue());
    //         dashboard.setTotal_wholesale_price(((Number) result[6]).doubleValue());
    //         dashboard.setTotal_revenue(((Number) result[7]).doubleValue());

    //         dashboardData.add(dashboard);
    //     }

    //     return dashboardData;
    // }
    
    

}
