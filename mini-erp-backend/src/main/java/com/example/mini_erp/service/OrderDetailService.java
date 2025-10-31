package com.example.mini_erp.service;

import com.example.mini_erp.entity.OrderDetail;
import com.example.mini_erp.repository.OrderDetailRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class OrderDetailService {

    private final OrderDetailRepository orderDetailRepository;

    public OrderDetailService(OrderDetailRepository orderDetailRepository) {
        this.orderDetailRepository = orderDetailRepository;
    }

    // 查詢所有訂單詳情
    public List<OrderDetail> getAllOrderDetails() {
        return orderDetailRepository.findAll();
    }

    // 根據ID查詢訂單詳情
    public Optional<OrderDetail> getOrderDetailById(int orderDetailId) {
        return orderDetailRepository.findById(orderDetailId);
    }

    // 根據訂單ID查詢訂單詳情
    public List<OrderDetail> getOrderDetailsByOrderId(int orderId) {
        return orderDetailRepository.findByOrderOrderId(orderId);
    }

    // 新增訂單詳情
    @Transactional
    public OrderDetail addOrderDetail(OrderDetail orderDetail) {
        return orderDetailRepository.save(orderDetail);
    }

    // 更新訂單詳情
    @Transactional
    public OrderDetail updateOrderDetail(int orderDetailId, OrderDetail orderDetail) {
        if (!orderDetailRepository.existsById(orderDetailId)) {
            throw new RuntimeException("OrderDetail not found");
        }
        orderDetail.setOrderDetailId(orderDetailId);
        return orderDetailRepository.save(orderDetail);
    }

    // 刪除訂單詳情
    @Transactional
    public void deleteOrderDetail(int orderDetailId) {
        if (!orderDetailRepository.existsById(orderDetailId)) {
            throw new RuntimeException("OrderDetail not found");
        }
        orderDetailRepository.deleteById(orderDetailId);
    }
}