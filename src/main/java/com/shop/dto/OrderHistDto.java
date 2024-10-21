package com.shop.dto;

import com.shop.constant.OrderStatus;
import com.shop.entity.Order;
import lombok.Getter;
import lombok.Setter;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OrderHistDto {

    // 주문 정보를 담는 객체

    public OrderHistDto(Order order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getOrderStatus();

        // OrderHistDto 생성자, order 객체 파라미터 받아서 멤버 변수 값 세팅, 주문 날짜인 경우 화면에 년, 월, 일 형태로 전달하기 위해서 포맷을 수정
    }

    private Long orderId;   // 주문 아이디

    private String orderDate;   // 주문 날찌

    private OrderStatus orderStatus;    // 주문 상태

    // 주문 상태 리스트
    private List<OrderItemDto> orderItemDtoList = new ArrayList<>();

    public void addOrderItemDto(OrderItemDto orderItemDto) {
        // OrderItemDto 객체를 주문 상품 리스트에 추가
        orderItemDtoList.add(orderItemDto);
    }
}
