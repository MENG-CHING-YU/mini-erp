package com.example.mini_erp.entity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import jakarta.persistence.Transient;

import java.math.BigDecimal;

import lombok.Data;


@Data
@Entity
@Table(name = "OrderDetails")
public class OrderDetail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "OrderDetailID")
    private Integer orderDetailId;

    @ManyToOne
    @JoinColumn(name = "OrderID", nullable = false)
    private Order order;

    @ManyToOne
    @JoinColumn(name = "ProductID", nullable = false)
    private Product product;

    @Column(name = "Quantity", nullable = false)
    private Integer quantity;

    @Column(name = "UnitPrice", nullable = false)
    private BigDecimal unitPrice;

    // 計算小計金額（這部分不會存儲在資料庫）
    @Transient
    public BigDecimal getTotalAmount() {
        return unitPrice.multiply(new BigDecimal(quantity));
    }
}
