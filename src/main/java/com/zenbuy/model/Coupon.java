package com.zenbuy.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String discountType; // "PERCENTAGE" or "FLAT"

    @Column(nullable = false)
    private Double discountValue; // Percentage (0-100) or flat amount

    @Column(nullable = false)
    private LocalDateTime validFrom;

    @Column(nullable = false)
    private LocalDateTime validUntil;

    @Column(nullable = false)
    private Boolean isActive = true;

    private Integer maxUses; // null means unlimited
    private Integer usedCount = 0;

    private Double minPurchaseAmount; // Minimum order amount to use this coupon

    @Column(columnDefinition = "TEXT")
    private String description;
}

