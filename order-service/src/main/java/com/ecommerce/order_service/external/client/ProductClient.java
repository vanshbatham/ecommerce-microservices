package com.ecommerce.order_service.external.client;

import com.ecommerce.order_service.external.response.ProductDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "product-service")
public interface ProductClient {

    @GetMapping("/api/v1/products/sku/{sku}")
    ProductDto getProductBySku(@PathVariable("sku") String sku);

    @PutMapping("/api/v1/products/sku/{sku}/reduce-stock")
    void reduceStock(@PathVariable("sku") String sku, @RequestParam("quantity") Integer quantity);
}