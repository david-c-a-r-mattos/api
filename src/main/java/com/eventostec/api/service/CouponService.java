
package com.eventostec.api.service;

import com.eventostec.api.domain.coupon.Coupon;
import com.eventostec.api.domain.coupon.CouponRequestDTO;
import com.eventostec.api.domain.event.Event;
import com.eventostec.api.repositories.CouponRepository;
import com.eventostec.api.repositories.EventRepository;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouponService 
{
    @Autowired
    private CouponRepository couponRepository;
    @Autowired
    private EventRepository eventRepository;
    public Coupon addCouponToEvent(UUID eventId, CouponRequestDTO couponData)
    {
        Event event = eventRepository.findById(eventId).orElseThrow(() -> new IllegalArgumentException("Event not Fouund!"));
        Coupon coupon = new Coupon();
        coupon.setCode(couponData.getCode());
        coupon.setDiscount(couponData.getDiscount());
        coupon.setValid(new Date(couponData.getValid()));
        coupon.setEvent(event);
        return couponRepository.save(coupon);
    }
    public List<Coupon> consultCoupons(UUID eventId, Date currentDate)
    {
        return couponRepository.findByEventIdAndValidAfter(eventId, currentDate);
    }
}
