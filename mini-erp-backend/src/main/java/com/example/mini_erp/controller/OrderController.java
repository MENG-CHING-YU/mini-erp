package com.example.mini_erp.controller;

import com.example.mini_erp.entity.Order;
import com.example.mini_erp.service.OrderService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    // 查詢所有訂單
    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();  // 返回 204 No Content，如果沒有訂單
        }
        return ResponseEntity.ok(orders);
    }

    // 根據ID查詢訂單
    @GetMapping("/{orderId}")
    public ResponseEntity<Order> getOrderById(@PathVariable int orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        return order.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());  // 返回 404 如果找不到訂單
    }

    // 創建訂單
    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            return ResponseEntity.status(201).body(createdOrder);  // 返回 201 Created
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);  // 返回 400 如果庫存不足或其他錯誤
        }
    }

    // 更新訂單狀態
    @PutMapping("/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(
            @PathVariable int orderId, 
            @RequestParam String status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            return ResponseEntity.ok(updatedOrder);  // 返回 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();  // 返回 404 如果訂單不存在
        }
    }

    // 刪除訂單
    @DeleteMapping("/{orderId}")
    public ResponseEntity<Void> deleteOrder(@PathVariable int orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.noContent().build();  // 返回 204 No Content 成功刪除
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();  // 返回 404 如果訂單不存在
        }
    }
}
