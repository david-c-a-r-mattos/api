package com.eventostec.api.domain.event;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public record EventDetailsDTO(
    UUID Id,
    String title,
    String description,
    Date date,
    String city,
    String uf,
    String imgUrl,
    String eventUrl,
    List<CouponDTO> Coupons)
    {
        public record CouponDTO(
           String code,
           Integer discount,
           Date validUtil){
    }
        
}
