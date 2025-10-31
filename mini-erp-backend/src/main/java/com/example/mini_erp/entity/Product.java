package com.example.mini_erp.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.math.BigDecimal;
import java.util.Date;

@Data
@Entity
@Table(name = "Products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ProductID")
    private Integer productId;

    @Column(name = "ProductName", nullable = false)
    private String productName;

    @Column(name = "ProductDescription")
    private String productDescription;

    @Column(name = "UnitPrice", nullable = false)
    private BigDecimal unitPrice;

    @Column(name = "Category")
    private String category;

    @Column(name = "CreatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
}
