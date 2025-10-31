package com.example.mini_erp.controller;

import com.example.mini_erp.entity.InventoryTransaction;
import com.example.mini_erp.service.InventoryTransactionService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory-transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;

    public InventoryTransactionController(InventoryTransactionService inventoryTransactionService) {
        this.inventoryTransactionService = inventoryTransactionService;
    }

    // 查詢庫存交易紀錄（根據產品ID）
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryTransaction>> getTransactionsByProductId(@PathVariable int productId) {
        List<InventoryTransaction> transactions = inventoryTransactionService.getTransactionsByProductId(productId);
        if (transactions.isEmpty()) {
            return ResponseEntity.notFound().build();  // 返回 404，如果找不到交易紀錄
        }
        return ResponseEntity.ok(transactions);
    }

    // 記錄庫存交易
    @PostMapping("/create")
    public ResponseEntity<InventoryTransaction> createTransaction(
            @RequestParam int productId,
            @RequestParam int quantity,
            @RequestParam String transactionType,
            @RequestParam(required = false) Integer orderId) {
        try {
            InventoryTransaction transaction = inventoryTransactionService.createTransaction(
                    productId, quantity, transactionType, orderId);
            return ResponseEntity.ok(transaction);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);  // 如果發生錯誤，返回 400
        }
    }
}
