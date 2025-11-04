package com.example.mini_erp.util;

import com.example.mini_erp.dto.*;
import com.example.mini_erp.entity.*;
import org.springframework.stereotype.Component;

@Component
public class DtoConverter {

    // Product 轉換
    public ProductDTO toProductDTO(Product product) {
        if (product == null) return null;
        ProductDTO dto = new ProductDTO();
        dto.setProductId(product.getProductId());
        dto.setProductName(product.getProductName());
        dto.setProductDescription(product.getProductDescription());
        dto.setUnitPrice(product.getUnitPrice());
        dto.setCategory(product.getCategory());
        return dto;
    }

    public Product toProductEntity(ProductDTO dto) {
        if (dto == null) return null;
        Product product = new Product();
        product.setProductId(dto.getProductId());
        product.setProductName(dto.getProductName());
        product.setProductDescription(dto.getProductDescription());
        product.setUnitPrice(dto.getUnitPrice());
        product.setCategory(dto.getCategory());
        return product;
    }

    // Inventory 轉換
    public InventoryDTO toInventoryDTO(Inventory inventory) {
        if (inventory == null) return null;
        InventoryDTO dto = new InventoryDTO();
        dto.setProductId(inventory.getProductId());
        dto.setStockQuantity(inventory.getStockQuantity());
        dto.setLastUpdated(new java.sql.Date(inventory.getLastUpdated().getTime()));
        return dto;
    }

    // Order 轉換
    public OrderDTO toOrderDTO(Order order) {
        if (order == null) return null;
        OrderDTO dto = new OrderDTO();
        dto.setOrderId(order.getOrderId());
        dto.setOrderDate(new java.sql.Date(order.getOrderDate().getTime()));
        dto.setCustomerName(order.getCustomerName());
        dto.setCustomerEmail(order.getCustomerEmail());
        dto.setTotalAmount(order.getTotalAmount());
        dto.setStatus(order.getStatus());
        dto.setShippingAddress(order.getShippingAddress());
        dto.setPaymentStatus(order.getPaymentStatus());
        dto.setCreatedAt(new java.sql.Date(order.getCreatedAt().getTime()));
        return dto;
    }

    public Order toOrderEntity(OrderDTO dto) {
        if (dto == null) return null;
        Order order = new Order();
        order.setOrderId(dto.getOrderId());
        if (dto.getOrderDate() != null) {
            order.setOrderDate(new java.util.Date(dto.getOrderDate().getTime()));
        }
        order.setCustomerName(dto.getCustomerName());
        order.setCustomerEmail(dto.getCustomerEmail());
        order.setTotalAmount(dto.getTotalAmount());
        order.setStatus(dto.getStatus());
        order.setShippingAddress(dto.getShippingAddress());
        order.setPaymentStatus(dto.getPaymentStatus());
        return order;
    }

    // OrderDetail 轉換
    public OrderDetailDTO toOrderDetailDTO(OrderDetail orderDetail) {
        if (orderDetail == null) return null;
        OrderDetailDTO dto = new OrderDetailDTO();
        dto.setOrderDetailId(orderDetail.getOrderDetailId());
        dto.setOrderId(orderDetail.getOrder().getOrderId());
        dto.setProductId(orderDetail.getProduct().getProductId());
        dto.setQuantity(orderDetail.getQuantity());
        dto.setUnitPrice(orderDetail.getUnitPrice());
        return dto;
    }

    // InventoryTransaction 轉換
    public InventoryTransactionDTO toInventoryTransactionDTO(InventoryTransaction transaction) {
        if (transaction == null) return null;
        InventoryTransactionDTO dto = new InventoryTransactionDTO();
        dto.setTransactionId(transaction.getTransactionId());
        dto.setProductId(transaction.getProduct().getProductId());
        dto.setQuantity(transaction.getQuantity());
        dto.setTransactionType(transaction.getTransactionType());
        dto.setOrderId(transaction.getOrder() != null ? transaction.getOrder().getOrderId() : null);
        dto.setTransactionDate(new java.sql.Date(transaction.getTransactionDate().getTime()));
        dto.setNotes(transaction.getNotes());
        return dto;
    }
}