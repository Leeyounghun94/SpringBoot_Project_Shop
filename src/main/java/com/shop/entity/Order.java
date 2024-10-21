package com.shop.entity;


import com.shop.constant.OrderStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity{

    @Id
    @GeneratedValue
    @Column(name = "order_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member ;
    // 한명의 회원이 여러번 주문을 할 수가 있기 때문에 다대일 단방향 매핑을 한다.

    private LocalDateTime orderDate;    // 주문일

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;    // 주문 상태

    //private LocalDateTime updateTime ;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)  //부모 엔티티 영속성 상태변화를 자식 엔티티에게 모두 전덜하는 cascade = CascadeType.ALL
    // orphanRemoval = true -> 고아 객체 제거

    private List<OrderItem> orderItems = new ArrayList<>();// 하나의 주문이 여러개 상품을 가질수 있으므로 List자료형을 사용하여 매핑한다.

    public void addOrderItem(OrderItem orderItem) {
        // orderItems 에다가 주문 상품 정보 담기
        orderItems.add(orderItem);
        orderItem.setOrder(this);   // order엔티티 + orderitem엔티티가 양방향 참조 관계, orderitem객체에도 order 셋팅
    }

    public static Order createOrder(Member member, List<OrderItem> orderItemList) {

        Order order = new Order();

        order.setMember(member);    // 상품을 주문한 회원 정보 셋팅

        for (OrderItem orderItem : orderItemList) {
            // 상품페이지에서는 1개만 주문하지만 장바구니 페이지에서는 여러 상품을 주문 할 수 있으니 여러 상품을 담을 수 있는 리스트 형태로 파라미터 값을 받으며 주문 객체에 orderitem객체 추가

            order.addOrderItem(orderItem);
        }

        order.setOrderStatus(OrderStatus.ORDER);    // 주문 상태를 order로 셋팅
        order.setOrderDate(LocalDateTime.now());    // 현재 시간을 주문 시간으로 셋팅

        return order;
    }


    public int getTotalPrice() {    // 총 주문 금액을 구하는 메서드

        int totalPrice = 0 ;

        for (OrderItem orderItem : orderItems) {

            totalPrice += orderItem.getTotalPrice();

        }

        return totalPrice;
    }

    public void cancelOrder() {
        // 주문 취소 시 주문 수량을 상품의 재고에 더해주는 로직과 주문 상태를 취소 상태로 만드는 메서드

        this.orderStatus = OrderStatus.CANCEL;

        for (OrderItem orderItem : orderItems) {

            orderItem.cancel();
        }
    }
}
