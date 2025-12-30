package com.zenbuy.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnore
    private User user;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<OrderItem> items = new ArrayList<>();

    @Column(nullable = false)
    private double subtotal;

    @Column(nullable = false)
    private double shipping;

    @Column(nullable = false)
    private double discount;

    @Column(nullable = false)
    private double total;

    @Column(nullable = false)
    private String paymentMethod;

    @Column(nullable = false)
    private String paymentStatus;

    @Column(nullable = false)
    private String status = "CONFIRMED"; // PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED

    @Column(nullable = false)
    private LocalDateTime orderDate;

    // Shipping address fields
    @Column(nullable = false)
    private String shippingFlatNo;

    @Column(nullable = false)
    private String shippingLocality;

    @Column(nullable = false)
    private String shippingCity;

    @Column(nullable = false)
    private String shippingPincode;

    @Column(nullable = true)
    private String orderNumber;

    @Column(nullable = true)
    private String transactionId; // For PayU and other payment gateways

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
        if (orderNumber == null || orderNumber.isEmpty()) {
            orderNumber = "ORD-" + System.currentTimeMillis();
        }
        if (status == null || status.isEmpty()) {
            status = "CONFIRMED";
        }
    }
}

