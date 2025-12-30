package com.zenbuy.controller;

import com.zenbuy.model.User;
import com.zenbuy.model.Coupon;
import com.zenbuy.repository.UserRepository;
import com.zenbuy.repository.ProductRepository;
import com.zenbuy.repository.CouponRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final CouponRepository couponRepository;

    public AdminController(UserRepository userRepository, ProductRepository productRepository, CouponRepository couponRepository) {
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.couponRepository = couponRepository;
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getStats() {
        Map<String, Object> stats = new HashMap<>();
        long userCount = userRepository.count();
        long productCount = productRepository.count();
        stats.put("totalUsers", userCount);
        stats.put("totalProducts", productCount);
        stats.put("totalOrders", 0L); // Orders not implemented yet
        stats.put("revenue", 0.0); // Orders not implemented yet
        System.out.println("Admin stats requested - Users: " + userCount + ", Products: " + productCount);
        return ResponseEntity.ok(stats);
    }

    @GetMapping("/users")
    public ResponseEntity<List<Map<String, Object>>> getAllUsers() {
        List<User> users = userRepository.findAll();
        // Return only necessary fields, exclude sensitive/large data
        List<Map<String, Object>> userList = users.stream().map(user -> {
            Map<String, Object> userData = new HashMap<>();
            userData.put("id", user.getId());
            userData.put("name", user.getName());
            userData.put("email", user.getEmail());
            userData.put("role", user.getRole() != null ? user.getRole() : "USER");
            userData.put("provider", user.getProvider() != null ? user.getProvider() : "local");
            userData.put("providerId", user.getProviderId());
            // Don't include profilePic, password, address fields, etc.
            return userData;
        }).collect(Collectors.toList());
        return ResponseEntity.ok(userList);
    }

    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            if (!userRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            userRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping("/coupons")
    public ResponseEntity<List<Coupon>> getAllCoupons() {
        List<Coupon> coupons = couponRepository.findAll();
        return ResponseEntity.ok(coupons);
    }

    @PostMapping("/coupons")
    public ResponseEntity<?> createCoupon(@RequestBody CouponRequest request) {
        try {
            // Check if coupon code already exists
            if (couponRepository.findByCode(request.getCode().toUpperCase()).isPresent()) {
                Map<String, String> error = new HashMap<>();
                error.put("error", "Coupon code already exists");
                return ResponseEntity.badRequest().body(error);
            }

            Coupon coupon = new Coupon();
            coupon.setCode(request.getCode().toUpperCase());
            coupon.setDiscountType(request.getDiscountType());
            coupon.setDiscountValue(request.getDiscountValue());
            coupon.setValidFrom(request.getValidFrom() != null ? request.getValidFrom() : LocalDateTime.now());
            coupon.setValidUntil(request.getValidUntil());
            coupon.setIsActive(request.getIsActive() != null ? request.getIsActive() : true);
            coupon.setMaxUses(request.getMaxUses());
            coupon.setMinPurchaseAmount(request.getMinPurchaseAmount());
            coupon.setDescription(request.getDescription());

            Coupon savedCoupon = couponRepository.save(coupon);
            return ResponseEntity.ok(savedCoupon);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    @DeleteMapping("/coupons/{id}")
    public ResponseEntity<?> deleteCoupon(@PathVariable Long id) {
        try {
            if (!couponRepository.existsById(id)) {
                return ResponseEntity.notFound().build();
            }
            couponRepository.deleteById(id);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/coupons/{id}")
    public ResponseEntity<?> updateCoupon(@PathVariable Long id, @RequestBody CouponRequest request) {
        try {
            Optional<Coupon> couponOpt = couponRepository.findById(id);
            if (couponOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            Coupon coupon = couponOpt.get();
            
            // Check if code is being changed and if new code already exists
            if (!coupon.getCode().equals(request.getCode().toUpperCase())) {
                if (couponRepository.findByCode(request.getCode().toUpperCase()).isPresent()) {
                    Map<String, String> error = new HashMap<>();
                    error.put("error", "Coupon code already exists");
                    return ResponseEntity.badRequest().body(error);
                }
            }

            coupon.setCode(request.getCode().toUpperCase());
            coupon.setDiscountType(request.getDiscountType());
            coupon.setDiscountValue(request.getDiscountValue());
            if (request.getValidFrom() != null) coupon.setValidFrom(request.getValidFrom());
            if (request.getValidUntil() != null) coupon.setValidUntil(request.getValidUntil());
            if (request.getIsActive() != null) coupon.setIsActive(request.getIsActive());
            coupon.setMaxUses(request.getMaxUses());
            coupon.setMinPurchaseAmount(request.getMinPurchaseAmount());
            coupon.setDescription(request.getDescription());

            Coupon updatedCoupon = couponRepository.save(coupon);
            return ResponseEntity.ok(updatedCoupon);
        } catch (Exception e) {
            Map<String, String> error = new HashMap<>();
            error.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(error);
        }
    }

    // Inner class for coupon request DTO
    static class CouponRequest {
        private String code;
        private String discountType;
        private Double discountValue;
        private LocalDateTime validFrom;
        private LocalDateTime validUntil;
        private Boolean isActive;
        private Integer maxUses;
        private Double minPurchaseAmount;
        private String description;

        // Getters and setters
        public String getCode() { return code; }
        public void setCode(String code) { this.code = code; }
        public String getDiscountType() { return discountType; }
        public void setDiscountType(String discountType) { this.discountType = discountType; }
        public Double getDiscountValue() { return discountValue; }
        public void setDiscountValue(Double discountValue) { this.discountValue = discountValue; }
        public LocalDateTime getValidFrom() { return validFrom; }
        public void setValidFrom(LocalDateTime validFrom) { this.validFrom = validFrom; }
        public LocalDateTime getValidUntil() { return validUntil; }
        public void setValidUntil(LocalDateTime validUntil) { this.validUntil = validUntil; }
        public Boolean getIsActive() { return isActive; }
        public void setIsActive(Boolean isActive) { this.isActive = isActive; }
        public Integer getMaxUses() { return maxUses; }
        public void setMaxUses(Integer maxUses) { this.maxUses = maxUses; }
        public Double getMinPurchaseAmount() { return minPurchaseAmount; }
        public void setMinPurchaseAmount(Double minPurchaseAmount) { this.minPurchaseAmount = minPurchaseAmount; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
    }
}

