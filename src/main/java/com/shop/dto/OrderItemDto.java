package com.shop.dto;


import com.shop.entity.OrderItem;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderItemDto {

    // 조회한 주문 데이터를 화면에 보내기 위한 객체

    public OrderItemDto(OrderItem orderItem, String imgUrl) {
        // OrderItemDto 생성자로 orderItem, 이미지 경로를 파라미터로 받아 멤버 변수 값을 세팅한다.

        this.itemName = orderItem.getItem().getItemName();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imgUrl = imgUrl;
    }

    private String itemName; // 상품명

    private int count; // 주문 수량

    private int orderPrice; // 주문 금액

    private String imgUrl;  // 상품 이미지 경로
}
