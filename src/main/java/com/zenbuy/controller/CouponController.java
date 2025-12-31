package com.zenbuy.controller;

import com.zenbuy.model.Coupon;
import com.zenbuy.repository.CouponRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.context.annotation.Profile;


import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/coupons")
@CrossOrigin(origins = "*")
@Profile("dev")

public class CouponController {

    private final CouponRepository couponRepository;

    public CouponController(CouponRepository couponRepository) {
        this.couponRepository = couponRepository;
    }

    @GetMapping("/validate/{code}")
    public ResponseEntity<?> validateCoupon(@PathVariable String code, @RequestParam(required = false) Double orderAmount) {
        try {
            Optional<Coupon> couponOpt = couponRepository.findByCode(code.toUpperCase());
            
            if (couponOpt.isEmpty()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Invalid coupon code");
                return ResponseEntity.badRequest().body(error);
            }

            Coupon coupon = couponOpt.get();
            LocalDateTime now = LocalDateTime.now();

            // Check if coupon is active
            if (!coupon.getIsActive()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "This coupon is no longer active");
                return ResponseEntity.badRequest().body(error);
            }

            // Check validity period
            // Coupon is valid if: validFrom <= now <= validUntil (inclusive)
            if (coupon.getValidFrom() != null && now.isBefore(coupon.getValidFrom())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "This coupon is not yet valid");
                return ResponseEntity.badRequest().body(error);
            }
            // Check if expired: now must be <= validUntil (inclusive), so expired if now > validUntil
            if (coupon.getValidUntil() != null && now.isAfter(coupon.getValidUntil())) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "This coupon has expired");
                return ResponseEntity.badRequest().body(error);
            }

            // Check max uses
            if (coupon.getMaxUses() != null && coupon.getUsedCount() >= coupon.getMaxUses()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "This coupon has reached its usage limit");
                return ResponseEntity.badRequest().body(error);
            }

            // Check minimum purchase amount
            if (orderAmount != null && coupon.getMinPurchaseAmount() != null && orderAmount < coupon.getMinPurchaseAmount()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Minimum purchase amount of ₹" + coupon.getMinPurchaseAmount() + " required");
                return ResponseEntity.badRequest().body(error);
            }

            // Return coupon details
            Map<String, Object> response = new HashMap<>();
            response.put("valid", true);
            response.put("code", coupon.getCode());
            response.put("discountType", coupon.getDiscountType());
            response.put("discountValue", coupon.getDiscountValue());
            response.put("description", coupon.getDescription());

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @GetMapping("/active")
    public ResponseEntity<List<Coupon>> getActiveCoupons() {
        LocalDateTime now = LocalDateTime.now();
        List<Coupon> coupons = couponRepository.findByIsActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(now, now);
        return ResponseEntity.ok(coupons);
    }
}

