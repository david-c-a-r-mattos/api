package com.eventostec.api.repositories;

import com.eventostec.api.domain.coupon.Coupon;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 *
 * @author david
 */
public interface CouponRepository extends JpaRepository<Coupon, UUID>
{
    
}
