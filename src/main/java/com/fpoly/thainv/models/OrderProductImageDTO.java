package com.fpoly.thainv.models;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
@Getter
@Setter
@AllArgsConstructor
public class OrderProductImageDTO {
    private Integer orderId;
    private Integer orderDetailId;
    private Integer productId;
    private String productName;
    private Double unitPrice;
    private Integer quantity; 
    private String imgUrl;
}

