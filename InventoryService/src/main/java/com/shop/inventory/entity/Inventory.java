package com.shop.inventory.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory")
@Getter
@Setter
@NoArgsConstructor
public class Inventory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private Long productId;

    private String productName;

    /** Units that can still be sold. */
    @Column(nullable = false)
    private int availableQty;

    /** Units held for orders whose payment is not settled yet. */
    @Column(nullable = false)
    private int reservedQty;

    @Column(nullable = false)
    private int lowStockThreshold = 5;

    private LocalDateTime updatedAt;
}
