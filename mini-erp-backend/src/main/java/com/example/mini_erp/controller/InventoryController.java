package com.example.mini_erp.controller;

import com.example.mini_erp.dto.ErrorResponse;
import com.example.mini_erp.dto.InventoryDTO;
import com.example.mini_erp.entity.Inventory;
import com.example.mini_erp.service.InventoryService;
import com.example.mini_erp.util.DtoConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/inventory")
public class InventoryController {

    private final InventoryService inventoryService;
    private final DtoConverter dtoConverter;

    public InventoryController(InventoryService inventoryService, DtoConverter dtoConverter) {
        this.inventoryService = inventoryService;
        this.dtoConverter = dtoConverter;
    }

    // 查詢所有庫存
    @GetMapping
    public ResponseEntity<List<InventoryDTO>> getAllInventory() {
        List<Inventory> inventoryList = inventoryService.getAllInventory();
        List<InventoryDTO> inventoryDTOs = inventoryList.stream()
                .map(dtoConverter::toInventoryDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(inventoryDTOs);
    }

    // 根據產品ID查詢庫存
    @GetMapping("/{productId}")
    public ResponseEntity<?> getInventoryByProductId(@PathVariable int productId) {
        Optional<Inventory> inventory = inventoryService.getInventoryByProductId(productId);
        if (inventory.isPresent()) {
            InventoryDTO inventoryDTO = dtoConverter.toInventoryDTO(inventory.get());
            return ResponseEntity.ok(inventoryDTO);
        } else {
            ErrorResponse error = new ErrorResponse(404, "Not Found", "找不到產品庫存 ID: " + productId);
            return ResponseEntity.status(404).body(error);
        }
    }

    // 初始化庫存
    @PostMapping("/initialize/{productId}")
    public ResponseEntity<?> initializeInventory(
            @PathVariable int productId,
            @RequestParam int initialQuantity) {
        try {
            Inventory inventory = inventoryService.initializeInventory(productId, initialQuantity);
            InventoryDTO inventoryDTO = dtoConverter.toInventoryDTO(inventory);
            return ResponseEntity.ok(inventoryDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "初始化庫存失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 增加庫存
    @PutMapping("/increase/{productId}")
    public ResponseEntity<?> increaseStock(
            @PathVariable int productId,
            @RequestParam int quantity) {
        try {
            Inventory inventory = inventoryService.increaseStock(productId, quantity);
            InventoryDTO inventoryDTO = dtoConverter.toInventoryDTO(inventory);
            return ResponseEntity.ok(inventoryDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "增加庫存失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 減少庫存
    @PutMapping("/decrease/{productId}")
    public ResponseEntity<?> decreaseStock(
            @PathVariable int productId,
            @RequestParam int quantity) {
        try {
            Inventory inventory = inventoryService.decreaseStock(productId, quantity);
            InventoryDTO inventoryDTO = dtoConverter.toInventoryDTO(inventory);
            return ResponseEntity.ok(inventoryDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "減少庫存失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // 更新庫存數量
    @PutMapping("/update/{productId}")
    public ResponseEntity<?> updateStock(
            @PathVariable int productId,
            @RequestParam int quantity) {
        try {
            Inventory inventory = inventoryService.updateStock(productId, quantity, "UPDATE");
            InventoryDTO inventoryDTO = dtoConverter.toInventoryDTO(inventory);
            return ResponseEntity.ok(inventoryDTO);
        } catch (RuntimeException e) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", "更新庫存失敗: " + e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
      // 新增產品庫存
    @PostMapping("/add-product-stock")
    public ResponseEntity<?> addProductStock(@RequestBody InventoryDTO inventoryDTO) {
        try {
            Inventory inventory = inventoryService.addProductStock(inventoryDTO);
            InventoryDTO responseDTO = dtoConverter.toInventoryDTO(inventory);
            return ResponseEntity.status(201).body(responseDTO);
        } catch (Exception e) {
            ErrorResponse error = new ErrorResponse(400, "Bad Request", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }
}