package com.example.mini_erp.service;

import com.example.mini_erp.entity.Order;
import com.example.mini_erp.entity.OrderDetail;
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
        // 在創建訂單之前檢查庫存並更新庫存
        for (OrderDetail detail : order.getOrderDetails()) {
            inventoryService.decreaseStock(detail.getProduct().getProductId(), detail.getQuantity());
        }
        return orderRepository.save(order);
    }

    // 更新訂單狀態
    @Transactional
    public Order updateOrderStatus(int orderId, String status) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(status);
        return orderRepository.save(order);
    }

    // 刪除訂單
    @Transactional
    public void deleteOrder(int orderId) {
        if (!orderRepository.existsById(orderId)) {
            throw new RuntimeException("Order not found");
        }
        orderRepository.deleteById(orderId);
    }
}