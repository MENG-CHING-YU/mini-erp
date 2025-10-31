package com.example.mini_erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mini_erp.entity.InventoryTransaction;

import java.util.List;

public interface InventoryTransactionRepository extends JpaRepository<InventoryTransaction, Integer> {
    // 根據產品ID查詢庫存交易（使用關聯屬性名稱）
    List<InventoryTransaction> findByProductProductId(int productId);
    
    // 根據訂單ID查詢庫存交易
    List<InventoryTransaction> findByOrderOrderId(int orderId);
    
    // 根據交易類型查詢
    List<InventoryTransaction> findByTransactionType(String transactionType);
}