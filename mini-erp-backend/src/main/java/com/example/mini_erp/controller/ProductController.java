package com.example.mini_erp.controller;

import com.example.mini_erp.entity.Product;
import com.example.mini_erp.service.ProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    // 查詢所有產品
    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();  // 返回 204 No Content，如果沒有產品
        }
        return ResponseEntity.ok(products);
    }

    // 根據ID查詢產品
    @GetMapping("/{productId}")
    public ResponseEntity<Product> getProductById(@PathVariable int productId) {
        Optional<Product> product = productService.getProductById(productId);
        return product.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());  // 返回 404 如果找不到產品
    }

    // 新增產品
    @PostMapping
    public ResponseEntity<Product> addProduct(@RequestBody Product product) {
        try {
            Product createdProduct = productService.addProduct(product);
            return ResponseEntity.status(201).body(createdProduct);  // 返回 201 Created
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null);  // 返回 400 如果發生錯誤
        }
    }

    // 更新產品
    @PutMapping("/{productId}")
    public ResponseEntity<Product> updateProduct(
            @PathVariable int productId, 
            @RequestBody Product product) {
        try {
            Product updatedProduct = productService.updateProduct(productId, product);
            return ResponseEntity.ok(updatedProduct);  // 返回 200 OK
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();  // 返回 404 如果產品不存在
        }
    }

    // 刪除產品
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int productId) {
        try {
            productService.deleteProduct(productId);
            return ResponseEntity.noContent().build();  // 返回 204 No Content 成功刪除
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();  // 返回 404 如果產品不存在
        }
    }
}
