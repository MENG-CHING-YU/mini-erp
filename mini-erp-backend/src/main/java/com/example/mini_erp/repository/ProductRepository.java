package com.example.mini_erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mini_erp.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Integer> {
    // 可以在此處添加自定義查詢方法
}
