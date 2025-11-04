package com.example.mini_erp.service;

import com.example.mini_erp.entity.Inventory;
import com.example.mini_erp.exception.BusinessException;
import com.example.mini_erp.exception.InsufficientStockException;
import com.example.mini_erp.exception.ResourceNotFoundException;
import com.example.mini_erp.repository.InventoryRepository;
import com.example.mini_erp.repository.ProductRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryRepository inventoryRepository;
    private final ProductRepository productRepository;
    private final InventoryTransactionService inventoryTransactionService;

    public InventoryService(
            InventoryRepository inventoryRepository,
            ProductRepository productRepository,
            InventoryTransactionService inventoryTransactionService) {
        this.inventoryRepository = inventoryRepository;
        this.productRepository = productRepository;
        this.inventoryTransactionService = inventoryTransactionService;
    }

    // 查詢所有庫存
    public List<Inventory> getAllInventory() {
        return inventoryRepository.findAll();
    }

    // 根據產品ID查詢庫存
    public Optional<Inventory> getInventoryByProductId(int productId) {
        return inventoryRepository.findById(productId);
    }

    // 更新庫存數量
    @Transactional
    public Inventory updateStock(int productId, int quantity, String transactionType) {
        Inventory inventory = inventoryRepository.findById(productId)
            .orElseGet(() -> {
                // 如果庫存不存在，創建新庫存
                Inventory newInventory = new Inventory();
                newInventory.setProductId(productId);
                newInventory.setStockQuantity(0);
                return newInventory;
            });

        // 更新庫存數量
        int newQuantity = inventory.getStockQuantity() + quantity;
        if (newQuantity < 0) {
            throw new InsufficientStockException(
                String.format("產品 ID %d 庫存不足，當前庫存: %d，需要: %d", 
                    productId, inventory.getStockQuantity(), Math.abs(quantity))
            );
        }

        inventory.setStockQuantity(newQuantity);
        inventory.setLastUpdated(new Date());

        // 記錄庫存交易
        inventoryTransactionService.createTransaction(productId, quantity, transactionType, null);

        return inventoryRepository.save(inventory);
    }

    // 增加庫存
    @Transactional
    public Inventory increaseStock(int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("增加數量必須大於 0");
        }
        return updateStock(productId, quantity, "IN");
    }

    // 減少庫存
    @Transactional
    public Inventory decreaseStock(int productId, int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("減少數量必須大於 0");
        }
        return updateStock(productId, -quantity, "OUT");
    }

    // 初始化產品庫存
    @Transactional
    public Inventory initializeInventory(int productId, int initialQuantity) {
        // 檢查產品是否存在
        productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("產品不存在，ID: " + productId));

        // 檢查庫存是否已存在
        if (inventoryRepository.existsById(productId)) {
            throw new BusinessException("產品 ID " + productId + " 的庫存已經初始化過了");
        }

        if (initialQuantity < 0) {
            throw new IllegalArgumentException("初始庫存數量不能為負數");
        }

        Inventory inventory = new Inventory();
        inventory.setProductId(productId);
        inventory.setStockQuantity(initialQuantity);
        inventory.setLastUpdated(new Date());

        // 記錄初始庫存交易
        inventoryTransactionService.createTransaction(productId, initialQuantity, "INITIAL", null);

        return inventoryRepository.save(inventory);
    }
}