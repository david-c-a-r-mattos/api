package com.eventostec.api.repositories;

import com.eventostec.api.domain.coupon.Coupon;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CouponRepository extends JpaRepository<Coupon, UUID>
{
    List<Coupon> findByEventIdAndValidAfter(UUID eventId, Date currentDate);
}
