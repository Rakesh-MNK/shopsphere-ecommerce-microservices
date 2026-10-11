package com.shop.product.controller;

import com.shop.product.dto.ProductRequest;
import com.shop.product.dto.ProductResponse;
import com.shop.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    // ---------- ADMIN ----------
    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProductResponse> create(@Valid @RequestBody ProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.create(request));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ProductResponse update(@PathVariable Long id, @Valid @RequestBody ProductRequest request) {
        return productService.update(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public Map<String, String> delete(@PathVariable Long id) {
        productService.delete(id);
        return Map.of("message", "Product " + id + " deleted");
    }

    // ---------- CUSTOMER / ADMIN ----------
    @GetMapping
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public List<ProductResponse> getAll(@RequestParam(required = false) String category) {
        return productService.getAll(category);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('CUSTOMER','ADMIN')")
    public ProductResponse getById(@PathVariable Long id) {
        return productService.getById(id);
    }
}
