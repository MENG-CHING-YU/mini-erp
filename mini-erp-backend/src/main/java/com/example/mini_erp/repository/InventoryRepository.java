package com.example.mini_erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.mini_erp.entity.Inventory;

public interface InventoryRepository extends JpaRepository<Inventory, Integer> {
    // 根據需要，你也可以定義一些自定義查詢方法
}

