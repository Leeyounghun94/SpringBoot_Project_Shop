package com.shop.service;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.dto.OrderItemDto;
import com.shop.entity.*;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import com.shop.repository.OrderRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class OrderService {

    // 주문 로직 서비스

    private final ItemRepository itemRepository;// 주문 할 상품을 조회하기

    private final MemberRepository memberRepository;

    private final OrderRepository orderRepository;

    private final ItemImgRepository itemImgRepository;

    public Long order(OrderDto orderDto, String email) {
        // 주문자의 이메일과 오더를 받아 아이템을 찾음.
        Item item = itemRepository.findById(orderDto.getItemId())
                .orElseThrow(EntityNotFoundException::new);// 주문 할 상품을 조회하기

        Member member = memberRepository.findByEmail(email);// 현재 로그인한 회원의 이메일 정보를 이용해서 회원 정보 조회

        List<OrderItem> orderItemList = new ArrayList<>();  // 주문자의 주문이 다수임으로 리스트로 처리함.
        OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());// 주문할 상품 엔티티 +  주문 수량 이용하여 주문상품 엔티티 생성
        orderItemList.add(orderItem);
        Order order = Order.createOrder(member, orderItemList);// 회원정보 + 주문할 상품 리스트 정보 이용해서 주문 엔티티 생성
        orderRepository.save(order);

        return order.getId();
    }

    @Transactional(readOnly = true)  // 313 추가 (OrderControll에서 호출 됨)
    public Page<OrderHistDto> getOrderList(String email, Pageable pageable) {

        List<Order> orders = orderRepository.findOrders(email, pageable);// 유저 아이디와 페이징 조건을 이용해서 주문 목록 조회
        Long totalCount = orderRepository.countOrder(email);// 유저의 주문 총 개수 구한다.

        List<OrderHistDto> orderHistDtos = new ArrayList<>();

        for (Order order : orders) {// 주문 리스트가 돌면서 구매 이력 페이지에 전달한 DTO를 생성
            OrderHistDto orderHistDto = new OrderHistDto(order);
            List<OrderItem> orderItems = order.getOrderItems();
            for (OrderItem orderItem : orderItems) {
                ItemImg itemImg = itemImgRepository.findByItemIdAndRepimgYn
                        (orderItem.getItem().getId(), "Y");// 주문한 상품의 대표 이미지 조회
                OrderItemDto orderItemDto =
                        new OrderItemDto(orderItem, itemImg.getImgUrl());
                orderHistDto.addOrderItemDto(orderItemDto);
            }

            orderHistDtos.add(orderHistDto);
        }

        return new PageImpl<OrderHistDto>(orderHistDtos, pageable, totalCount);// 페이지 구현 객체 생성해서 반환
    }


    @Transactional(readOnly = true)
    public boolean validateOrder(Long orderId, String email) {
        // 주문을 취소하는 메서드
        // 현재 로그인한 사용자와 주문 데이터를 생성한 사용자가 같은지 검사한다. 같은때는 true , 같지 않을때는 false를 반환함

        Member curMember = memberRepository.findByEmail(email);
        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);
        Member savedMember = order.getMember();


        if (!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())) {

            return false;
        }

        return true;

    }

    public void cancelOrder(Long orderId) {

        Order order = orderRepository.findById(orderId).orElseThrow(EntityNotFoundException::new);

        order.cancelOrder();// 주문 취소 상태로 변경하면 변경 감지에 의해 트랜젝션이끝날 때 update 쿼리 발동.
    }


    public Long orders(List<OrderDto> orderDtoList, String email) {

        Member member = memberRepository.findByEmail(email);
        List<OrderItem> orderItemList = new ArrayList<>();

        for (OrderDto orderDto : orderDtoList) {
            // 주문할 상품 리스트 만들어 준다

            Item item = itemRepository.findById(orderDto.getItemId()).orElseThrow(EntityNotFoundException::new);

            OrderItem orderItem = OrderItem.createOrderItem(item, orderDto.getCount());

            orderItemList.add(orderItem);
        }

        Order order = Order.createOrder(member, orderItemList);
        // 현재 로그인한 회원과 주문 상품 목록을 이용하여 주문엔티티 생성
        orderRepository.save(order);
        // 주문 데이터 저장
        return  order.getId();
    }
}