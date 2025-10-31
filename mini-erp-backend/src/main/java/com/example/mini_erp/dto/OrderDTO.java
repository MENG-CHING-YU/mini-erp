package com.example.mini_erp.dto;

import java.math.BigDecimal;
import java.sql.Date;

import lombok.Data;

@Data
public class OrderDTO {
    private Integer orderId;
    private Date orderDate;
    private String customerName;
    private String customerEmail;
    private BigDecimal totalAmount;
    private String status;
    private String shippingAddress;
    private String paymentStatus;
    private Date createdAt;
}

