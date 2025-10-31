package com.example.mini_erp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class OrderDetailDTO {
    private Integer orderDetailId;
    private Integer orderId;
    private Integer productId;
    private Integer quantity;
    private BigDecimal unitPrice;
}
