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

    // ⚠️ 移除手動建立交易紀錄的 API
    // 交易紀錄應該只由系統自動產生（在庫存變動時）
    // 如果需要手動調整，應該透過 InventoryController 的增加/減少庫存 API
}