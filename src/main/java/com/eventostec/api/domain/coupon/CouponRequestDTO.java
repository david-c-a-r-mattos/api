
package com.eventostec.api.domain.coupon;

public class CouponRequestDTO {
    private String code;
    private Integer discount;
    private Long valid;
    
    
    // Construtor padrão obrigatório
    public CouponRequestDTO() {}

    public CouponRequestDTO(String code, Integer discount, Long valid) 
    {
        this.code = code;
        this.discount = discount;
        this.valid = valid;
    }
    
    // Getters e Setters obrigatórios

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Integer getDiscount() {
        return discount;
    }

    public void setDiscount(Integer discount) {
        this.discount = discount;
    }

    public Long getValid() {
        return valid;
    }

    public void setValid(Long valide) {
        this.valid = valide;
    }
   
}