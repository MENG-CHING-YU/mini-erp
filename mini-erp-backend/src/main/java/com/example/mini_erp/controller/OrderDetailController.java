package com.example.mini_erp.controller;

import com.example.mini_erp.entity.OrderDetail;
import com.example.mini_erp.service.OrderDetailService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    public OrderDetailController(OrderDetailService orderDetailService) {
        this.orderDetailService = orderDetailService;
    }

    // 查詢所有訂單詳情
    @GetMapping
    public ResponseEntity<List<OrderDetail>> getAllOrderDetails() {
        List<OrderDetail> orderDetails = orderDetailService.getAllOrderDetails();
        if (orderDetails.isEmpty()) {
            return ResponseEntity.noContent().build();  // 返回 204 No Content，如果沒有訂單詳情
        }
        return ResponseEntity.ok(orderDetails);
    }

    // 根據ID查詢訂單詳情
    @GetMapping("/{orderDetailId}")
    public ResponseEntity<OrderDetail> getOrderDetailById(@PathVariable int orderDetailId) {
        Optional<OrderDetail> orderDetail = orderDetailService.getOrderDetailById(orderDetailId);
        return orderDetail.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());  // 返回 404 如果找不到訂單詳情
    }

    // 根據訂單ID查詢訂單詳情
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetail>> getOrderDetailsByOrderId(@PathVariable int orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        if (orderDetails.isEmpty()) {
            return ResponseEntity.noContent().build();  // 返回 204 No Content 如果沒有該訂單的詳情
        }
        return ResponseEntity.ok(orderDetails);
    }

    // 新增訂單詳情
    @PostMapping
    public ResponseEntity<OrderDetail> addOrderDetail(@RequestBody OrderDetail orderDetail) {
        try {
            OrderDetail createdOrderDetail = orderDetailService.addOrderDetail(orderDetail);
            return ResponseEntity.status(201).body(createdOrderDetail);  // 返回 201 Created
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);  // 返回 400 如果發生錯誤
        }
    }

    // 更新訂單詳情
    @PutMapping("/{orderDetailId}")
    public ResponseEntity<OrderDetail> updateOrderDetail(
            @PathVariable int orderDetailId, 
            @RequestBody OrderDetail orderDetail) {
        try {
            OrderDetail updatedOrderDetail = orderDetailService.updateOrderDetail(orderDetailId, orderDetail);
            return ResponseEntity.ok(updatedOrderDetail);  // 返回 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();  // 返回 404 如果訂單詳情不存在
        }
    }

    // 刪除訂單詳情
    @DeleteMapping("/{orderDetailId}")
    public ResponseEntity<Void> deleteOrderDetail(@PathVariable int orderDetailId) {
        try {
            orderDetailService.deleteOrderDetail(orderDetailId);
            return ResponseEntity.noContent().build();  // 返回 204 No Content 成功刪除
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();  // 返回 404 如果訂單詳情不存在
        }
    }
}
