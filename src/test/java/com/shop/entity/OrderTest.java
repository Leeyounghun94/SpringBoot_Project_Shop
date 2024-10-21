package com.shop.entity;


import com.shop.constant.ItemSellStatus;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderItemRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class OrderTest {

    @Autowired
    OrderRepository orderRepository;

    @Autowired
    ItemRepository itemRepository;

    @PersistenceContext
    EntityManager em;

    public Item createItem() {

        Item item = new Item();
        item.setItemName("아이템1");
        item.setPrice(50000);
        item.setItemDetail("상세설명1");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        return item ;
    }

    @Test
    @DisplayName("영속성 전달 테스트")
    public void cascadeTest() {

        Order order = new Order();

        for (int i=0; i<3; i++) {
            Item item = this.createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();
            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(50000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);   // orderItem을 order엔티티에 담아주기
        }

        orderRepository.saveAndFlush(order);    // order엔티티 저장하면서 flush메서드 호출하여 db에 저장시킨다.
        em.clear(); // 영속성 상태 초기화

        Order savedOrder = orderRepository.findById(order.getId())
                .orElseThrow(EntityNotFoundException::new);
        Assertions.assertEquals(3, savedOrder.getOrderItems().size());
        //ItemOrder 엔티티 3개가 실제로 db에 저장되는지 검사하기

        /* Hibernate:
            insert
            into
                orders
                (member_id, order_date, order_status, update_time, order_id)
            values
                (?, ?, ?, ?, ?)

               -> flush 호출 시 orders 테이블에 insert


           Hibernate:
            insert
            into
                order_item
                (count, item_id, order_id, order_price, reg_time, update_time, order_item_id)
            values
                (?, ?, ?, ?, ?, ?, ?)

                > flush 호출 시 order_item 테이블에 insert
         */
    }


    @Autowired
    MemberRepository memberRepository;

    public Order createOrder() {// 주문 데이터 생성해서 저장하는 메서드

        Order order = new Order();

        for (int i=0; i<3; i++) {

            Item item = createItem();
            itemRepository.save(item);

            OrderItem orderItem = new OrderItem();

            orderItem.setItem(item);
            orderItem.setCount(10);
            orderItem.setOrderPrice(1000);
            orderItem.setOrder(order);
            order.getOrderItems().add(orderItem);
        }

        Member member = new Member();
        memberRepository.save(member);

        order.setMember(member);
        orderRepository.save(order);

        return order;
    }


    @Test
    @DisplayName("고아 제거 테스트")
    public void orphanRemoveTest() {
        Order order = this.createOrder();
        order.getOrderItems().remove(0);// 엔티티에서 관리하는 orderitem 리스트의 0번 인덱스를 제거
        em.flush();

        /* Hibernate:
            delete
            from
                order_item
            where
                order_item_id=?

                고아 객체가 될 경우 delete 쿼리문 실행
         */
    }


    @Autowired
    OrderItemRepository orderItemRepository;

    @Test
    @DisplayName("지연 로딩 테스트")
    public void lazyLoadingTest() {
        Order order = this.createOrder();
        Long orderItemId = order.getOrderItems().get(0).getId();
        em.flush();
        em.clear();

     /*   OrderItem orderItem = orderItemRepository.findById(orderItemId) // 초기화 후 order엔티티 저장했던 상품 아이디 이용해서 orderItem을 db에서 다시 조회..
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("Order class : " + orderItem.getOrder().getClass()); */

        OrderItem orderItem = orderItemRepository.findById(orderItemId) // 초기화 후 order엔티티 저장했던 상품 아이디 이용해서 orderItem을 db에서 다시 조회..
                .orElseThrow(EntityNotFoundException::new);

        System.out.println("Order class : " + orderItem.getOrder().getClass());
        System.out.println("===============================================================");
        orderItem.getOrder().getOrderDate();
        System.out.println("===============================================================");


    }
    /* Hibernate:
            select
                oi1_0.order_item_id,
                oi1_0.count,
                i1_0.item_id,
                i1_0.item_detail,
                i1_0.item_name,
                i1_0.item_sell_status,
                i1_0.price,
                i1_0.reg_time,
                i1_0.stock_number,
                i1_0.update_time,
                o1_0.order_id,
                m1_0.member_id,
                m1_0.address,
                m1_0.email,
                m1_0.name,
                m1_0.password,
                m1_0.role,
                o1_0.order_date,
                o1_0.order_status,
                o1_0.update_time,
                oi1_0.order_price,
                oi1_0.reg_time,
                oi1_0.update_time
            from
                order_item oi1_0
            left join
                item i1_0
                    on i1_0.item_id=oi1_0.item_id
            left join
                orders o1_0
                    on o1_0.order_id=oi1_0.order_id
            left join
                member m1_0
                    on m1_0.member_id=o1_0.member_id
            where
                oi1_0.order_item_id=?

                즉시 로딩을 통해 사용하지 않는 데이터들도 한꺼번에 들고 오는걸 확인,
                이렇게 사용하지 않는 데이터들도 한꺼번에 조회하니 성능문제와 개발자가 쿼리 예측하기가 어렵다. 그래서 즉시 로딩은 실무에서는 사용하기 어렵다(실무에서는 매핑되는 엔티티 개수 훨씬 많으니)




     Order class : class com.shop.entity.Order$HibernateProxy$WyO7vr2f      -> 지연 로딩 설정 후 orderItem에 매핑된 Order클래스 출력 결과(지연로딩하면 엔티티 대신 프록시 객체를 넣어둠)
        ===============================================================
        Hibernate:
            select
                o1_0.order_id,
                m1_0.member_id,
                m1_0.address,
                m1_0.email,
                m1_0.name,
                m1_0.password,
                m1_0.role,
                o1_0.order_date,
                o1_0.order_status,
                o1_0.update_time
            from
                orders o1_0
            left join
                member m1_0
                    on m1_0.member_id=o1_0.member_id
            where
                o1_0.order_id=?
        ===============================================================
                -> 프록시 객체 사용 시점에 조회 쿼리문이 실행하여 OrderDate 조회할때 select 실행
     */
}
