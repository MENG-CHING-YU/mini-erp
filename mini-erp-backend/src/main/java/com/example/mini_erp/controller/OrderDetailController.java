package com.example.mini_erp.controller;

import com.example.mini_erp.dto.ErrorResponse;
import com.example.mini_erp.dto.OrderDetailDTO;
import com.example.mini_erp.entity.OrderDetail;
import com.example.mini_erp.service.OrderDetailService;
import com.example.mini_erp.util.DtoConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/order-details")
public class OrderDetailController {

    private final OrderDetailService orderDetailService;
    private final DtoConverter dtoConverter;

    public OrderDetailController(OrderDetailService orderDetailService, DtoConverter dtoConverter) {
        this.orderDetailService = orderDetailService;
        this.dtoConverter = dtoConverter;
    }

    // 查詢所有訂單詳情
    @GetMapping
    public ResponseEntity<List<OrderDetailDTO>> getAllOrderDetails() {
        List<OrderDetail> orderDetails = orderDetailService.getAllOrderDetails();
        if (orderDetails.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<OrderDetailDTO> orderDetailDTOs = orderDetails.stream()
                .map(dtoConverter::toOrderDetailDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDetailDTOs);
    }

    // 根據ID查詢訂單詳情
    @GetMapping("/{orderDetailId}")
    public ResponseEntity<?> getOrderDetailById(@PathVariable int orderDetailId) {
        Optional<OrderDetail> orderDetail = orderDetailService.getOrderDetailById(orderDetailId);
        if (orderDetail.isPresent()) {
            OrderDetailDTO orderDetailDTO = dtoConverter.toOrderDetailDTO(orderDetail.get());
            return ResponseEntity.ok(orderDetailDTO);
        } else {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "找不到訂單詳情 ID: " + orderDetailId);
            return ResponseEntity.status(404).body(error);
        }
    }

    // 根據訂單ID查詢訂單詳情
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetailDTO>> getOrderDetailsByOrderId(@PathVariable int orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        if (orderDetails.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<OrderDetailDTO> orderDetailDTOs = orderDetails.stream()
                .map(dtoConverter::toOrderDetailDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(orderDetailDTOs);
    }

    // 新增訂單詳情
    @PostMapping
    public ResponseEntity<?> addOrderDetail(@RequestBody OrderDetail orderDetail) {
        try {
            OrderDetail createdOrderDetail = orderDetailService.addOrderDetail(orderDetail);
            OrderDetailDTO orderDetailDTO = dtoConverter.toOrderDetailDTO(createdOrderDetail);
            return ResponseEntity.status(201).body(orderDetailDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "新增訂單詳情失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 更新訂單詳情
    @PutMapping("/{orderDetailId}")
    public ResponseEntity<?> updateOrderDetail(
            @PathVariable int orderDetailId, 
            @RequestBody OrderDetail orderDetail) {
        try {
            OrderDetail updatedOrderDetail = orderDetailService.updateOrderDetail(orderDetailId, orderDetail);
            OrderDetailDTO orderDetailDTO = dtoConverter.toOrderDetailDTO(updatedOrderDetail);
            return ResponseEntity.ok(orderDetailDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "更新訂單詳情失敗: " + e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }

    // 刪除訂單詳情
    @DeleteMapping("/{orderDetailId}")
    public ResponseEntity<?> deleteOrderDetail(@PathVariable int orderDetailId) {
        try {
            orderDetailService.deleteOrderDetail(orderDetailId);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "刪除訂單詳情失敗: " + e.getMessage());
            return ResponseEntity.status(404).body(error);
        }
    }
}