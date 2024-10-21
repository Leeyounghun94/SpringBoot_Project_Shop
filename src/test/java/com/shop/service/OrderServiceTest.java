package com.shop.service;


import com.shop.constant.ItemSellStatus;
import com.shop.constant.OrderStatus;
import com.shop.dto.OrderDto;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.entity.Order;
import com.shop.entity.OrderItem;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations="classpath:application-test.properties")
public class OrderServiceTest {


    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository;


    public Item saveItem() {
        // 테스트를 위해 상품 정보 저장 메서드

        Item item = new Item();
        item.setItemName("개상품");
        item.setPrice(777777);
        item.setItemDetail("이 상품은 이제 제 것입니다");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);

        return  itemRepository.save(item);
    }


    public Member saveMember() {
        // 테스트를 위해 유저 정보 저장 메서드

        Member member = new Member();

        member.setEmail("kkk@kkk.com");

        return memberRepository.save(member);
    }



    @Test
    @DisplayName("오더 테스트")
    public void order() {

        Item item = saveItem();
        Member member = saveMember();

        OrderDto orderDto = new OrderDto();

        orderDto.setCount(10);
        // 주문할 수량을 orderDTO에다가 셋팅
        orderDto.setItemId(item.getId());
        // 주문 상품을 orderDTO에다가 셋팅

        Long orderId = orderService.order(orderDto, member.getEmail());
        // 주문 로직 호출 결과 생성된 주문 번호를 orderId 변수에 저장하기.

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        // 주문 번호 이용해서 저장된 주문 정보 조회하기

        List<OrderItem> orderItems = order.getOrderItems();

        int totalPrice = orderDto.getCount()*item.getPrice();
        // 주문한 상품의 총 가격을 구한다.

        assertEquals(totalPrice, order.getTotalPrice());
        // 주문하 상품의 총 가격과 db에 저장된 상품의 가격을 비교해서 같으면 테스트가 성공!
    }
    /*
    Hibernate:
    select
        next value for item_seq
Hibernate:
    select
        next value for member_seq
Hibernate:
    insert
    into
        item
        (created_by, item_detail, item_name, item_sell_status, modified_by, price, reg_time, stock_number, update_time, item_id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate:
    insert
    into
        member
        (address, created_by, email, modified_by, name, password, reg_time, role, update_time, member_id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate:
    select
        m1_0.member_id,
        m1_0.address,
        m1_0.created_by,
        m1_0.email,
        m1_0.modified_by,
        m1_0.name,
        m1_0.password,
        m1_0.reg_time,
        m1_0.role,
        m1_0.update_time
    from
        member m1_0
    where
        m1_0.email=?
Hibernate:
    select
        next value for orders_seq
Hibernate:
    select
        next value for order_item_seq

     */

    @Test
    @DisplayName("주문 취소")
    public void cancelOrder() {

        Item item = saveItem();
        Member member = saveMember();
        // 상품과 회원 데이터 생성하고 생성한 상품의 재고는 100개~

        OrderDto orderDto = new OrderDto();
        orderDto.setCount(10);
        orderDto.setItemId(item.getId());
        Long orderId = orderService.order(orderDto, member.getEmail());
        // 주문 데이터도 생성하기 주문 개수는 총 10개

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);// 생성한 주문 엔티티 조회하기
        orderService.cancelOrder(orderId);// 해당 주문을 취소해버리기

        assertEquals(OrderStatus.CANCEL, order.getOrderStatus());// 주문 상태가 취소 상태라면 테스트 통과~
        assertEquals(100, item.getStockNumber());
        // 취소 후 상품의 재고가 처음 재고 개수인 100개와 동일하다면 테스트 통과~
    }
    /*
    Hibernate:
    select
        next value for item_seq
Hibernate:
    select
        next value for member_seq
Hibernate:
    insert
    into
        item
        (created_by, item_detail, item_name, item_sell_status, modified_by, price, reg_time, stock_number, update_time, item_id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate:
    insert
    into
        member
        (address, created_by, email, modified_by, name, password, reg_time, role, update_time, member_id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate:
    select
        m1_0.member_id,
        m1_0.address,
        m1_0.created_by,
        m1_0.email,
        m1_0.modified_by,
        m1_0.name,
        m1_0.password,
        m1_0.reg_time,
        m1_0.role,
        m1_0.update_time
    from
        member m1_0
    where
        m1_0.email=?
Hibernate:
    select
        next value for orders_seq
Hibernate:
    select
        next value for order_item_seq
     */
}
