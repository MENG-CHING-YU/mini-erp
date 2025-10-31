package com.example.mini_erp.controller;

import com.example.mini_erp.entity.Inventory;
import com.example.mini_erp.service.InventoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;

    public InventoryController(InventoryService inventoryService) {
        this.inventoryService = inventoryService;
    }

    // 查詢所有庫存
    @GetMapping
    public ResponseEntity<List<Inventory>> getAllInventory() {
        List<Inventory> inventoryList = inventoryService.getAllInventory();
        return ResponseEntity.ok(inventoryList);
    }

    // 根據產品ID查詢庫存
    @GetMapping("/{productId}")
    public ResponseEntity<Inventory> getInventoryByProductId(@PathVariable int productId) {
        Optional<Inventory> inventory = inventoryService.getInventoryByProductId(productId);
        return inventory.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // 初始化庫存
    @PostMapping("/initialize/{productId}")
    public ResponseEntity<Inventory> initializeInventory(
            @PathVariable int productId,
            @RequestParam int initialQuantity) {
        try {
            Inventory inventory = inventoryService.initializeInventory(productId, initialQuantity);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 增加庫存
    @PutMapping("/increase/{productId}")
    public ResponseEntity<Inventory> increaseStock(
            @PathVariable int productId,
            @RequestParam int quantity) {
        try {
            Inventory inventory = inventoryService.increaseStock(productId, quantity);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 減少庫存
    @PutMapping("/decrease/{productId}")
    public ResponseEntity<Inventory> decreaseStock(
            @PathVariable int productId,
            @RequestParam int quantity) {
        try {
            Inventory inventory = inventoryService.decreaseStock(productId, quantity);
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    // 更新庫存數量
    @PutMapping("/update/{productId}")
    public ResponseEntity<Inventory> updateStock(
            @PathVariable int productId,
            @RequestParam int quantity) {
        try {
            Inventory inventory = inventoryService.updateStock(productId, quantity, "UPDATE");
            return ResponseEntity.ok(inventory);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);
        }
    }
}
