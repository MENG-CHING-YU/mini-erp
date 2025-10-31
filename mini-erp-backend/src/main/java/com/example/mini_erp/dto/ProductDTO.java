package com.example.mini_erp.dto;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProductDTO {
    private Integer productId;
    private String productName;
    private String productDescription;
    private BigDecimal unitPrice;
    private String category;
}
