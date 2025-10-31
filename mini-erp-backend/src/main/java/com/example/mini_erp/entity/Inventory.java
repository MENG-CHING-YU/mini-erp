package com.example.mini_erp.entity;

import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;

import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;

import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;
@Data
@Entity
@Table(name = "Inventory")
public class Inventory {
    @Id
    @Column(name = "ProductID")
    private Integer productId;

    @OneToOne
    @JoinColumn(name = "ProductID", insertable = false, updatable = false)
    private Product product;

    @Column(name = "StockQuantity", nullable = false)
    private Integer stockQuantity = 0;

    @Column(name = "LastUpdated")
    @Temporal(TemporalType.TIMESTAMP)
    private Date lastUpdated = new Date();
}
