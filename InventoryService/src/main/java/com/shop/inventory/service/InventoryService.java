package com.shop.inventory.service;

import com.shop.inventory.dto.ProductEvent;
import com.shop.inventory.dto.StockResponse;
import com.shop.inventory.entity.Inventory;
import com.shop.inventory.event.LowStockPublisher;
import com.shop.inventory.exception.BusinessException;
import com.shop.inventory.exception.ResourceNotFoundException;
import com.shop.inventory.repository.InventoryRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private static final Logger log = LoggerFactory.getLogger(InventoryService.class);

    private final InventoryRepository inventoryRepository;
    private final LowStockPublisher lowStockPublisher;

    // ------------------------------------------------------------------ Kafka driven
    @Transactional
    public void handleProductEvent(ProductEvent event) {
        switch (event.eventType()) {
            case "PRODUCT_CREATED" -> {
                if (inventoryRepository.findByProductId(event.productId()).isEmpty()) {
                    Inventory inv = new Inventory();
                    inv.setProductId(event.productId());
                    inv.setProductName(event.productName());
                    inv.setAvailableQty(event.initialStock() == null ? 0 : event.initialStock());
                    inv.setReservedQty(0);
                    inv.setUpdatedAt(LocalDateTime.now());
                    inventoryRepository.save(inv);
                    log.info("Inventory created for product {} with stock {}", inv.getProductId(), inv.getAvailableQty());
                }
            }
            case "PRODUCT_UPDATED" -> inventoryRepository.findByProductId(event.productId()).ifPresent(inv -> {
                inv.setProductName(event.productName());
                inv.setUpdatedAt(LocalDateTime.now());
                inventoryRepository.save(inv);
            });
            case "PRODUCT_DELETED" -> inventoryRepository.findByProductId(event.productId())
                    .ifPresent(inventoryRepository::delete);
            default -> log.warn("Unknown product event type {}", event.eventType());
        }
    }

    // ------------------------------------------------------------------ Stock operations
    /** Moves quantity from available to reserved (order created, payment pending). */
    @Transactional
    public StockResponse reserve(Long productId, int quantity) {
        Inventory inv = lockOrThrow(productId);
        if (inv.getAvailableQty() < quantity) {
            throw new BusinessException("Insufficient stock for '%s'. Requested=%d, available=%d"
                    .formatted(inv.getProductName(), quantity, inv.getAvailableQty()));
        }
        inv.setAvailableQty(inv.getAvailableQty() - quantity);
        inv.setReservedQty(inv.getReservedQty() + quantity);
        inv.setUpdatedAt(LocalDateTime.now());
        Inventory saved = inventoryRepository.save(inv);
        if (saved.getAvailableQty() <= saved.getLowStockThreshold()) {
            lowStockPublisher.publish(saved);
        }
        return StockResponse.from(saved);
    }

    /** Payment failed: reserved quantity goes back to available (quantity adding). */
    @Transactional
    public StockResponse release(Long productId, int quantity) {
        Inventory inv = lockOrThrow(productId);
        if (inv.getReservedQty() < quantity) {
            throw new BusinessException("Cannot release %d units, only %d reserved".formatted(quantity, inv.getReservedQty()));
        }
        inv.setReservedQty(inv.getReservedQty() - quantity);
        inv.setAvailableQty(inv.getAvailableQty() + quantity);
        inv.setUpdatedAt(LocalDateTime.now());
        return StockResponse.from(inventoryRepository.save(inv));
    }

    /** Payment success: reserved quantity is permanently deducted (quantity minus). */
    @Transactional
    public StockResponse confirm(Long productId, int quantity) {
        Inventory inv = lockOrThrow(productId);
        if (inv.getReservedQty() < quantity) {
            throw new BusinessException("Cannot confirm %d units, only %d reserved".formatted(quantity, inv.getReservedQty()));
        }
        inv.setReservedQty(inv.getReservedQty() - quantity);
        inv.setUpdatedAt(LocalDateTime.now());
        return StockResponse.from(inventoryRepository.save(inv));
    }

    /** Adds units back to available stock (order cancelled after payment, or admin stock-in). */
    @Transactional
    public StockResponse addStock(Long productId, int quantity) {
        Inventory inv = lockOrThrow(productId);
        inv.setAvailableQty(inv.getAvailableQty() + quantity);
        inv.setUpdatedAt(LocalDateTime.now());
        return StockResponse.from(inventoryRepository.save(inv));
    }

    @Transactional(readOnly = true)
    public StockResponse get(Long productId) {
        return inventoryRepository.findByProductId(productId).map(StockResponse::from)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product " + productId));
    }

    @Transactional(readOnly = true)
    public List<StockResponse> getAll() {
        return inventoryRepository.findAll().stream().map(StockResponse::from).toList();
    }

    private Inventory lockOrThrow(Long productId) {
        return inventoryRepository.findByProductIdForUpdate(productId)
                .orElseThrow(() -> new ResourceNotFoundException("Inventory not found for product " + productId));
    }
}
