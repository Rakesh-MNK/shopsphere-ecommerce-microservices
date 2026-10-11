package com.shop.product.service;

import com.shop.product.dto.ProductEvent;
import com.shop.product.dto.ProductRequest;
import com.shop.product.dto.ProductResponse;
import com.shop.product.entity.Product;
import com.shop.product.event.ProductEventPublisher;
import com.shop.product.exception.ResourceNotFoundException;
import com.shop.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductEventPublisher eventPublisher;
    @InjectMocks
    private ProductService productService;

    @Test
    void create_savesProductAndPublishesCreatedEventWithInitialStock() {
        when(productRepository.save(any(Product.class))).thenAnswer(inv -> {
            Product p = inv.getArgument(0);
            p.setId(10L);
            return p;
        });

        ProductResponse response = productService.create(
                new ProductRequest("Laptop", "Gaming", new BigDecimal("999.99"), "Electronics", 25));

        assertEquals(10L, response.id());
        ArgumentCaptor<ProductEvent> captor = ArgumentCaptor.forClass(ProductEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertEquals("PRODUCT_CREATED", captor.getValue().eventType());
        assertEquals(25, captor.getValue().initialStock());
    }

    @Test
    void getById_missing_throwsNotFound() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> productService.getById(99L));
    }

    @Test
    void delete_publishesDeletedEvent() {
        Product p = new Product();
        p.setId(5L);
        p.setName("Mouse");
        p.setPrice(new BigDecimal("10.00"));
        when(productRepository.findById(5L)).thenReturn(Optional.of(p));

        productService.delete(5L);

        ArgumentCaptor<ProductEvent> captor = ArgumentCaptor.forClass(ProductEvent.class);
        verify(eventPublisher).publish(captor.capture());
        assertEquals("PRODUCT_DELETED", captor.getValue().eventType());
    }
}
