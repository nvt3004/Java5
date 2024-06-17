package com.fpoly.thainv.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DashbordList {
    private int id;
    private int id2;
    private Date order_date;
    private Double total_total_amount;
    private Double total_import_price;
    private Double total_retail_price;
    private Double total_wholesale_price;
    private Double total_revenue;
}
