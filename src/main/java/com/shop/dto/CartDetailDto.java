package com.shop.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartDetailDto {

    private Long cartItemId ; // 장바구니 상품 아이디

    private String itemName ; // 상품 이름

    private int price, count ; // 금액, 수량

    private String imgUrl ; // 상품 이미지 경로

    public CartDetailDto(Long cartItemId, String itemName, int price, int count, String imgUrl) {

        this.cartItemId = cartItemId;
        this.itemName = itemName;
        this.count = count;
        this.price = price ;
        this.imgUrl = imgUrl;
    }
}
