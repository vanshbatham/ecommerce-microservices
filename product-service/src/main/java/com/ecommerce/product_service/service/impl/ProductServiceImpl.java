package com.ecommerce.product_service.service.impl;

import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;
import com.ecommerce.product_service.entity.Product;
import com.ecommerce.product_service.exception.DuplicateResourceException;
import com.ecommerce.product_service.exception.ResourceNotFoundException;
import com.ecommerce.product_service.repository.ProductRepository;
import com.ecommerce.product_service.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product with SKU: {}", request.getSku());

        if (productRepository.existsBySku(request.getSku())) {
            throw new DuplicateResourceException("Product with this SKU already exists.");
        }

        Product product = Product.builder()
                .sku(request.getSku())
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .build();

        return mapToResponse(productRepository.save(product));
    }

    @Override
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
        return mapToResponse(product);
    }

    @Override
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");
        return productRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public void reduceStock(Long productId, Integer quantity) {
        log.info("Reducing stock for product id: {} by quantity: {}", productId, quantity);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + productId));

        if (product.getStockQuantity() < quantity) {
            throw new DuplicateResourceException("Insufficient stock for product ID: " + productId);
        }

        product.setStockQuantity(product.getStockQuantity() - quantity);
        if (product.getStockQuantity() == 0) {
            product.setAvailable(false);
        }
        productRepository.save(product);
    }

    private ProductResponse mapToResponse(Product product) {
        return ProductResponse.builder()
                .id(product.getId())
                .sku(product.getSku())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stockQuantity(product.getStockQuantity())
                .isAvailable(product.isAvailable())
                .createdAt(product.getCreatedAt())
                .build();
    }
}