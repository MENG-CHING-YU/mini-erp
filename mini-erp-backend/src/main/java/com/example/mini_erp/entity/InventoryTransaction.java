package com.example.mini_erp.entity;


import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.util.Date;
@Data
@Entity
@Table(name = "InventoryTransactions")
public class InventoryTransaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TransactionID")
    private Integer transactionId;

    @ManyToOne
    @JoinColumn(name = "ProductID", nullable = false)
    private Product product;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "TransactionType", nullable = false)
    private String transactionType;

    @ManyToOne
    @JoinColumn(name = "OrderID")
    private Order order;

    @Column(name = "TransactionDate")
    @Temporal(TemporalType.TIMESTAMP)
    private Date transactionDate = new Date();

    @Column(name = "Notes")
    private String notes;
}