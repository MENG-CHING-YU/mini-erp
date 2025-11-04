package com.example.mini_erp.controller;

import com.example.mini_erp.dto.ErrorResponse;
import com.example.mini_erp.dto.OrderDTO;
import com.example.mini_erp.entity.Order;
import com.example.mini_erp.service.OrderService;
import com.example.mini_erp.util.DtoConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    private final OrderService orderService;
    private final DtoConverter dtoConverter;

    public OrderController(OrderService orderService, DtoConverter dtoConverter) {
        this.orderService = orderService;
        this.dtoConverter = dtoConverter;
    }

    // 查詢所有訂單
    @GetMapping
    public ResponseEntity<List<OrderDTO>> getAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        if (orders.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<OrderDTO> orderDTOs = orders.stream()
                .map(dtoConverter::toOrderDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDTOs);
    }

    // 根據ID查詢訂單
    @GetMapping("/{orderId}")
    public ResponseEntity<?> getOrderById(@PathVariable int orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        if (order.isPresent()) {
            OrderDTO orderDTO = dtoConverter.toOrderDTO(order.get());
            return ResponseEntity.ok(orderDTO);
        } else {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "找不到訂單 ID: " + orderId);
            return ResponseEntity.status(404).body(error);
        }
    }

    // 創建訂單
    @PostMapping
    public ResponseEntity<?> createOrder(@RequestBody Order order) {
        try {
            Order createdOrder = orderService.createOrder(order);
            OrderDTO orderDTO = dtoConverter.toOrderDTO(createdOrder);
            return ResponseEntity.status(201).body(orderDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "創建訂單失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 更新訂單狀態
    @PutMapping("/{orderId}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable int orderId, 
            @RequestParam String status) {
        try {
            Order updatedOrder = orderService.updateOrderStatus(orderId, status);
            OrderDTO orderDTO = dtoConverter.toOrderDTO(updatedOrder);
            return ResponseEntity.ok(orderDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "更新訂單狀態失敗: " + e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    // 刪除訂單
    @DeleteMapping("/{orderId}")
    public ResponseEntity<?> deleteOrder(@PathVariable int orderId) {
        try {
            orderService.deleteOrder(orderId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "刪除訂單失敗: " + e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }
}