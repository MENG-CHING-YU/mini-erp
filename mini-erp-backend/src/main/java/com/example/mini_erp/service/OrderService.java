package com.example.mini_erp.service;

import com.example.mini_erp.entity.Order;
import com.example.mini_erp.entity.OrderDetail;
import com.example.mini_erp.exception.ResourceNotFoundException;
import com.example.mini_erp.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;
    private final InventoryService inventoryService;

    public OrderService(OrderRepository orderRepository, InventoryService inventoryService) {
        this.orderRepository = orderRepository;
        this.inventoryService = inventoryService;
    }

    // 查詢所有訂單
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    // 根據ID查詢訂單
    public Optional<Order> getOrderById(int orderId) {
        return orderRepository.findById(orderId);
    }

    // 新增訂單
    @Transactional
    public Order createOrder(Order order) {
        // 驗證訂單基本資訊
        if (order.getCustomerName() == null || order.getCustomerName().trim().isEmpty()) {
            throw new IllegalArgumentException("客戶姓名不能為空");
        }
        if (order.getOrderDetails() == null || order.getOrderDetails().isEmpty()) {
            throw new IllegalArgumentException("訂單明細不能為空");
        }
        
        // 在創建訂單之前檢查庫存並更新庫存
        for (OrderDetail detail : order.getOrderDetails()) {
            if (detail.getQuantity() <= 0) {
                throw new IllegalArgumentException("訂單數量必須大於 0");
            }
            inventoryService.decreaseStock(detail.getProduct().getProductId(), detail.getQuantity());
        }
        return orderRepository.save(order);
    }

    // 更新訂單狀態
    @Transactional
    public Order updateOrderStatus(int orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new ResourceNotFoundException("訂單不存在，ID: " + orderId));
        
        if (status == null || status.trim().isEmpty()) {
            throw new IllegalArgumentException("訂單狀態不能為空");
        }
        
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // 刪除訂單
    @Transactional
    public void deleteOrder(int orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new ResourceNotFoundException("訂單不存在，ID: " + orderId);
        }
        orderRepository.deleteById(orderId);
    }
}