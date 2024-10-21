package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CartOrderDto {

    private Long cartItemId;

    private List<CartOrderDto> cartOrderDtoList ;
    // 장바구니에서는 여러 개의 상품을 주문 하므로 cartOrderDTO클래스가 자기 자신을 리스트로 만들게 한다.

}
