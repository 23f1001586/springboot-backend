package com.zenbuy.repository;

import com.zenbuy.model.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    List<Coupon> findByIsActiveTrue();
    List<Coupon> findByIsActiveTrueAndValidFromLessThanEqualAndValidUntilGreaterThanEqual(
        LocalDateTime now1, LocalDateTime now2
    );
}

