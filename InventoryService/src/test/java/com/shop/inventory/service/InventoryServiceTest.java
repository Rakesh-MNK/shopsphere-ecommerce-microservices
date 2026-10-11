package com.shop.inventory.service;

import com.shop.inventory.dto.StockResponse;
import com.shop.inventory.entity.Inventory;
import com.shop.inventory.event.LowStockPublisher;
import com.shop.inventory.exception.BusinessException;
import com.shop.inventory.repository.InventoryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class InventoryServiceTest {

    @Mock
    private InventoryRepository inventoryRepository;
    @Mock
    private LowStockPublisher lowStockPublisher;
    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory(int available, int reserved) {
        Inventory inv = new Inventory();
        inv.setProductId(1L);
        inv.setProductName("Laptop");
        inv.setAvailableQty(available);
        inv.setReservedQty(reserved);
        inv.setLowStockThreshold(5);
        return inv;
    }

    private void stub(Inventory inv) {
        when(inventoryRepository.findByProductIdForUpdate(1L)).thenReturn(Optional.of(inv));
        when(inventoryRepository.save(any(Inventory.class))).thenAnswer(i -> i.getArgument(0));
    }

    @Test
    void reserve_movesQuantityFromAvailableToReserved() {
        stub(inventory(20, 0));
        StockResponse r = inventoryService.reserve(1L, 3);
        assertEquals(17, r.availableQty());
        assertEquals(3, r.reservedQty());
        verify(lowStockPublisher, never()).publish(any());
    }

    @Test
    void reserve_insufficientStock_throws() {
        stub(inventory(2, 0));
        assertThrows(BusinessException.class, () -> inventoryService.reserve(1L, 5));
    }

    @Test
    void reserve_belowThreshold_publishesLowStockEvent() {
        stub(inventory(8, 0));
        inventoryService.reserve(1L, 4);   // 4 left <= 5
        verify(lowStockPublisher).publish(any(Inventory.class));
    }

    @Test
    void confirm_deductsReservedQuantity() {
        stub(inventory(10, 4));
        StockResponse r = inventoryService.confirm(1L, 4);
        assertEquals(10, r.availableQty());
        assertEquals(0, r.reservedQty());
    }

    @Test
    void release_returnsReservedQuantityToAvailable() {
        stub(inventory(10, 4));
        StockResponse r = inventoryService.release(1L, 4);
        assertEquals(14, r.availableQty());
        assertEquals(0, r.reservedQty());
    }
}
