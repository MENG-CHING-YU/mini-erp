package com.example.mini_erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mini_erp.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    // 根據需要，你也可以定義一些自定義查詢方法
}

