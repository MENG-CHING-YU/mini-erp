package com.example.mini_erp.entity;

import lombok.Data;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Data
@Entity
@Table(name = "Orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderID")
    private Integer orderId;

    @Column(name = "OrderDate", nullable = false)
    @Temporal(TemporalType.TIMESTAMP)
    private Date orderDate = new Date();

    @Column(name = "CustomerName", nullable = false)
    private String customerName;

    @Column(name = "CustomerEmail")
    private String customerEmail;

    @Column(name = "TotalAmount", nullable = false)
    private BigDecimal totalAmount;

    @Column(name = "Status", nullable = false)
    private String status = "Pending";

    @Column(name = "ShippingAddress")
    private String shippingAddress;

    @Column(name = "PaymentStatus", nullable = false)
    private String paymentStatus = "Pending";

    @Column(name = "CreatedAt")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();
    
    // 添加與 OrderDetail 的一對多關聯
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderDetail> orderDetails = new ArrayList<>();
}