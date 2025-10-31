package com.example.mini_erp.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.example.mini_erp.entity.OrderDetail;

import java.util.List;

public interface OrderDetailRepository extends JpaRepository<OrderDetail, Integer> {
    // 根據訂單ID查詢訂單詳情（使用關聯屬性名稱）
    List<OrderDetail> findByOrderOrderId(int orderId);
    
    // 根據產品ID查詢訂單詳情
    List<OrderDetail> findByProductProductId(int productId);
}