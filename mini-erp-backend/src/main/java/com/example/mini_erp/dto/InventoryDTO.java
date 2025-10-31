package com.example.mini_erp.dto;

import java.sql.Date;

import lombok.Data;

@Data
public class InventoryDTO {
    private Integer productId;
    private Integer stockQuantity;
    private Date lastUpdated;
}

