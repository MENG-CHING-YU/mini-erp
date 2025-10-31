package com.example.mini_erp.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.mini_erp.entity.Product;
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
        return productRepository.save(product);
    }

    // 更新產品
    @Transactional
    public Product updateProduct(int productId, Product product) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        // 設定 productId（而非 setProductID）
        product.setProductId(productId);
        return productRepository.save(product);
    }

    // 刪除產品
    @Transactional
    public void deleteProduct(int productId) {
        if (!productRepository.existsById(productId)) {
            throw new RuntimeException("Product not found");
        }
        productRepository.deleteById(productId);
    }
}