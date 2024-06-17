package com.fpoly.thainv.entities;

import java.util.Date;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class DashbordList {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int id2;
    private Date orderDate;
    private Double totalTotalAmount;
    private Double totalImportPrice;
    private Double totalRetailPrice;
    private Double totalWholesalePrice;
    private Double totalRevenue;

    // Constructors, getters, setters, etc.
    
}
