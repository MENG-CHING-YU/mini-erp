package com.example.mini_erp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mini_erp.entity.Product;
import com.example.mini_erp.exception.ResourceNotFoundException;

import com.example.mini_erp.repository.ProductRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    // 查詢所有產品
    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    // 根據ID查詢產品
    public Optional<Product> getProductById(int productId) {
        return productRepository.findById(productId);
    }

    // 新增產品
    @Transactional
    public Product addProduct(Product product) {
        // 驗證必填欄位
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("產品名稱不能為空");
        }
        if (product.getUnitPrice() == null || product.getUnitPrice().doubleValue() < 0) {
            throw new IllegalArgumentException("產品價格不能為空或負數");
        }
        
        return productRepository.save(product);
    }

    // 更新產品
    @Transactional
    public Product updateProduct(int productId, Product product) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("產品不存在，ID: " + productId);
        }
        
        // 驗證必填欄位
        if (product.getProductName() == null || product.getProductName().trim().isEmpty()) {
            throw new IllegalArgumentException("產品名稱不能為空");
        }
        if (product.getUnitPrice() == null || product.getUnitPrice().doubleValue() < 0) {
            throw new IllegalArgumentException("產品價格不能為空或負數");
        }
        
        product.setProductId(productId);
        return productRepository.save(product);
    }

    // 刪除產品
    @Transactional
    public void deleteProduct(int productId) {
        if (!productRepository.existsById(productId)) {
            throw new ResourceNotFoundException("產品不存在，ID: " + productId);
        }
        productRepository.deleteById(productId);
    }
}