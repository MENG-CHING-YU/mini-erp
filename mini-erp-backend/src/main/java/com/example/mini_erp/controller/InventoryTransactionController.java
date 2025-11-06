package com.example.mini_erp.controller;

import com.example.mini_erp.dto.InventoryTransactionDTO;
import com.example.mini_erp.entity.InventoryTransaction;
import com.example.mini_erp.service.InventoryTransactionService;
import com.example.mini_erp.util.DtoConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory-transactions")
public class InventoryTransactionController {

    private final InventoryTransactionService inventoryTransactionService;
    private final DtoConverter dtoConverter;

    public InventoryTransactionController(InventoryTransactionService inventoryTransactionService, DtoConverter dtoConverter) {
        this.inventoryTransactionService = inventoryTransactionService;
        this.dtoConverter = dtoConverter;
    }

    // 查詢庫存交易紀錄（根據產品ID）
    @GetMapping("/product/{productId}")
    public ResponseEntity<List<InventoryTransactionDTO>> getTransactionsByProductId(@PathVariable int productId) {
        List<InventoryTransaction> transactions = inventoryTransactionService.getTransactionsByProductId(productId);
        if (transactions.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<InventoryTransactionDTO> transactionDTOs = transactions.stream()
                .map(dtoConverter::toInventoryTransactionDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(transactionDTOs);
    }
    // 查詢所有庫存交易紀錄
// 在 InventoryTransactionController.java 新增
@GetMapping
public ResponseEntity<List<InventoryTransactionDTO>> getAllTransactions() {
    List<InventoryTransaction> transactions = inventoryTransactionService.getAllTransactions();
    if (transactions.isEmpty()) {
        return ResponseEntity.noContent().build();
    }
    List<InventoryTransactionDTO> transactionDTOs = transactions.stream()
            .map(dtoConverter::toInventoryTransactionDTO)
            .collect(Collectors.toList());
    return ResponseEntity.ok(transactionDTOs);
}
}