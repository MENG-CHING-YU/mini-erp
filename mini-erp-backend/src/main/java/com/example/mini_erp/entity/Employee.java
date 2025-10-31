package com.example.mini_erp.entity;

import jakarta.persistence.*;
import lombok.Data;


import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "employees")
public class Employee {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "full_name", nullable = false)
    private String fullName;      // 員工姓名

    private String department;    // 部門
    private String position;      // 職位

    private String contact;       // 聯絡方式 (Email, Phone 組合成一個字串)

    @Column(name = "hire_date")
    private LocalDateTime hireDate = LocalDateTime.now(); // 入職日期

    // 外鍵對應 User 表
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = true)
    private User user;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // 每次更新資料時自動更新 updatedAt
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
