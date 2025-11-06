package com.example.mini_erp.service;

import com.example.mini_erp.entity.InventoryTransaction;
import com.example.mini_erp.entity.Product;
import com.example.mini_erp.entity.Order;
import com.example.mini_erp.repository.InventoryTransactionRepository;
import com.example.mini_erp.repository.ProductRepository;
import com.example.mini_erp.repository.OrderRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class InventoryTransactionService {

    private final InventoryTransactionRepository inventoryTransactionRepository;
    private final ProductRepository productRepository;
    private final OrderRepository orderRepository;

    public InventoryTransactionService(
            InventoryTransactionRepository inventoryTransactionRepository,
            ProductRepository productRepository,
            OrderRepository orderRepository) {
        this.inventoryTransactionRepository = inventoryTransactionRepository;
        this.productRepository = productRepository;
        this.orderRepository = orderRepository;
    }

    // 記錄庫存交易
    @Transactional
    public InventoryTransaction createTransaction(int productId, int quantity, String transactionType, Integer orderId) {
        InventoryTransaction transaction = new InventoryTransaction();
        
        // 設定 Product 物件（而非 ProductID）
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new RuntimeException("Product not found"));
        transaction.setProduct(product);
        
        transaction.setQuantity(quantity);
        transaction.setTransactionType(transactionType);
        
        // 如果有訂單ID，設定 Order 物件（而非 OrderID）
        if (orderId != null) {
            Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
            transaction.setOrder(order);
        }
        
        return inventoryTransactionRepository.save(transaction);
    }

    // 查詢庫存交易
    public List<InventoryTransaction> getTransactionsByProductId(int productId) {
        return inventoryTransactionRepository.findByProductProductId(productId);
    }

   public List<InventoryTransaction> getAllTransactions() {
    return inventoryTransactionRepository.findAll();
}
}