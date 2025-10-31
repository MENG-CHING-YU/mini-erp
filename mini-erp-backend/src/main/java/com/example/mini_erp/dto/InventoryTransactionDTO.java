package com.example.mini_erp.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class InventoryTransactionDTO {
    private Integer transactionId;
    private Integer productId;
    private Integer quantity;
    private String transactionType;
    private Integer orderId;
    private Date transactionDate;
    private String notes;
}

