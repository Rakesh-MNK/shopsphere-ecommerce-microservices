package com.shop.product.service;

import com.shop.product.dto.ProductEvent;
import com.shop.product.dto.ProductRequest;
import com.shop.product.dto.ProductResponse;
import com.shop.product.entity.Product;
import com.shop.product.event.ProductEventPublisher;
import com.shop.product.exception.ResourceNotFoundException;
import com.shop.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductEventPublisher eventPublisher;

    @Transactional
    public ProductResponse create(ProductRequest request) {
        Product product = new Product();
        apply(product, request);
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());
        Product saved = productRepository.save(product);
        eventPublisher.publish(new ProductEvent("PRODUCT_CREATED", saved.getId(), saved.getName(),
                saved.getPrice(), request.initialStock(), LocalDateTime.now()));
        return ProductResponse.from(saved);
    }

    @Transactional
    public ProductResponse update(Long id, ProductRequest request) {
        Product product = findOrThrow(id);
        apply(product, request);
        product.setUpdatedAt(LocalDateTime.now());
        Product saved = productRepository.save(product);
        eventPublisher.publish(new ProductEvent("PRODUCT_UPDATED", saved.getId(), saved.getName(),
                saved.getPrice(), null, LocalDateTime.now()));
        return ProductResponse.from(saved);
    }

    @Transactional
    public void delete(Long id) {
        Product product = findOrThrow(id);
        productRepository.delete(product);
        eventPublisher.publish(new ProductEvent("PRODUCT_DELETED", id, product.getName(),
                product.getPrice(), null, LocalDateTime.now()));
    }

    @Transactional(readOnly = true)
    public ProductResponse getById(Long id) {
        return ProductResponse.from(findOrThrow(id));
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> getAll(String category) {
        List<Product> products = (category == null || category.isBlank())
                ? productRepository.findAll()
                : productRepository.findByCategoryIgnoreCase(category);
        return products.stream().map(ProductResponse::from).toList();
    }

    private Product findOrThrow(Long id) {
        return productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));
    }

    private void apply(Product product, ProductRequest request) {
        product.setName(request.name().trim());
        product.setDescription(request.description());
        product.setPrice(request.price());
        product.setCategory(request.category().trim());
    }
}
