package com.fpoly.thainv.models;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MonthlyTotal {
    private int orderMonth;
    private int orderYear;
    private BigDecimal totalAmountPerMonth;
}
