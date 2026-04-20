package com.ecommerce.product_service.controller;


import com.ecommerce.product_service.dto.request.ProductRequest;
import com.ecommerce.product_service.dto.response.ProductResponse;
import com.ecommerce.product_service.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponse> createProduct(@Valid @RequestBody ProductRequest request) {
        return new ResponseEntity<>(productService.createProduct(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        return new ResponseEntity<>(productService.getProductById(id), HttpStatus.OK);
    }

    @GetMapping
    public ResponseEntity<List<ProductResponse>> getAllProducts() {
        return new ResponseEntity<>(productService.getAllProducts(), HttpStatus.OK);
    }
    
    @PutMapping("/{id}/reduce-stock")
    public ResponseEntity<Void> reduceStock(@PathVariable Long id, @RequestParam Integer quantity) {
        productService.reduceStock(id, quantity);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}