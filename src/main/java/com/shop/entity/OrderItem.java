package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;  // 아이템은 하나의 상품에서 여러 주문이 들어올수 있으니 다대일 단방향 매핑 설정한다.

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Order order;    // 한번의 주문으로 여러개 상품을 주문할수 있어서 주문상품과 주문 엔티티를 다대일 단방향으로 매핑.

    private int orderPrice; // 주문 가격

    private  int count ;    // 수량

   // private LocalDateTime regTime;

    // private LocalDateTime updateTime;

    public static OrderItem createOrderItem(Item item, int count) {

        OrderItem orderItem = new OrderItem();
        orderItem.setItem(item);
        orderItem.setCount(count);
        // 주문할 상품과 주문 수량을 셋팅

        orderItem.setOrderPrice(item.getPrice());
        // 현재 시각 기준으로 상품 가격을 주문 가격으로 셋팅, 상품 가격은 시간에 따라 다를 수 있으며 쿠폰이나 할인 이벤트로 유동적임

        item.removeStock(count);// 주문 수량 만큼 상품의 재고 수량을 감소시킨다.

        return orderItem;
    }

    public int getTotalPrice() {
        // 주문 가격 X 주문 수량 해서 상품 주문 한 총 가격을 계산하는 메서드

        return orderPrice*count;
    }

    public void  cancel() {
        // 주문 취소 시 주문 수량 만큼 재고를 더 해준다.

        this.getItem().addStock(count);

    }
}
