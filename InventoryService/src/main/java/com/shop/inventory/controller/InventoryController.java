package com.shop.inventory.controller;

import com.shop.inventory.dto.StockRequest;
import com.shop.inventory.dto.StockResponse;
import com.shop.inventory.service.InventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
public class InventoryController {

    private final InventoryService inventoryService;

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<StockResponse> getAll() {
        return inventoryService.getAll();
    }

    @GetMapping("/{productId}")
    public StockResponse get(@PathVariable Long productId) {
        return inventoryService.get(productId);
    }

    // ---- called by OrderService / PaymentService through OpenFeign (JWT is forwarded) ----
    @PostMapping("/reserve")
    public StockResponse reserve(@Valid @RequestBody StockRequest request) {
        return inventoryService.reserve(request.productId(), request.quantity());
    }

    @PostMapping("/release")
    public StockResponse release(@Valid @RequestBody StockRequest request) {
        return inventoryService.release(request.productId(), request.quantity());
    }

    @PostMapping("/confirm")
    public StockResponse confirm(@Valid @RequestBody StockRequest request) {
        return inventoryService.confirm(request.productId(), request.quantity());
    }

    @PostMapping("/restock")
    public StockResponse restock(@Valid @RequestBody StockRequest request) {
        return inventoryService.addStock(request.productId(), request.quantity());
    }

    // ---- admin stock-in ----
    @PostMapping("/add-stock")
    @PreAuthorize("hasRole('ADMIN')")
    public StockResponse addStock(@Valid @RequestBody StockRequest request) {
        return inventoryService.addStock(request.productId(), request.quantity());
    }
}
