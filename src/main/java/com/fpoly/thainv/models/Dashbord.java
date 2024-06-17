package com.fpoly.thainv.models;

import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Dashbord {
    private String img;
    private String productName;
    private String category;
    private Date orderDate;
    private Double unitPrice;
    private String statusName;
}
