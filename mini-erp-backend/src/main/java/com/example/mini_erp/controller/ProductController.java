package com.example.mini_erp.controller;


import com.example.mini_erp.dto.ProductDTO;
import com.example.mini_erp.entity.Product;
import com.example.mini_erp.exception.ResourceNotFoundException;
import com.example.mini_erp.service.ProductService;
import com.example.mini_erp.util.DtoConverter;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;
    private final DtoConverter dtoConverter;

    public ProductController(ProductService productService, DtoConverter dtoConverter) {
        this.productService = productService;
        this.dtoConverter = dtoConverter;
    }

    // 查詢所有產品
    @GetMapping
    public ResponseEntity<List<ProductDTO>> getAllProducts() {
        List<Product> products = productService.getAllProducts();
        if (products.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<ProductDTO> productDTOs = products.stream()
                .map(dtoConverter::toProductDTO)
                .collect(Collectors.toList());
        return ResponseEntity.ok(productDTOs);
    }

    // 根據ID查詢產品
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable int productId) {
        Product product = productService.getProductById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("找不到產品 ID: " + productId));
        ProductDTO productDTO = dtoConverter.toProductDTO(product);
        return ResponseEntity.ok(productDTO);
    }

    // 新增產品
    @PostMapping
    public ResponseEntity<ProductDTO> addProduct(@RequestBody ProductDTO productDTO) {
        Product product = dtoConverter.toProductEntity(productDTO);
        Product createdProduct = productService.addProduct(product);
        ProductDTO responseDTO = dtoConverter.toProductDTO(createdProduct);
        return ResponseEntity.status(201).body(responseDTO);
    }

    // 更新產品
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @PathVariable int productId, 
            @RequestBody ProductDTO productDTO) {
        Product product = dtoConverter.toProductEntity(productDTO);
        Product updatedProduct = productService.updateProduct(productId, product);
        ProductDTO responseDTO = dtoConverter.toProductDTO(updatedProduct);
        return ResponseEntity.ok(responseDTO);
    }

    // 刪除產品
    @DeleteMapping("/{productId}")
    public ResponseEntity<Void> deleteProduct(@PathVariable int productId) {
        productService.deleteProduct(productId);
        return ResponseEntity.noContent().build();
    }
}