package com.shop.service;

import com.shop.dto.CartDetailDto;
import com.shop.dto.CartItemDto;
import com.shop.dto.CartOrderDto;
import com.shop.dto.OrderDto;
import com.shop.entity.Cart;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.CartRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
@Log4j2
public class CartService {

    // 장바구니에 상품을 담는 로직 객체!

    private final ItemRepository itemRepository;

    private final MemberRepository memberRepository;

    private final CartRepository cartRepository;

    private final CartItemRepository cartItemRepository;

    private final OrderService orderService;


    public Long addCart(CartItemDto cartItemDto, String email) {

        Item item = itemRepository.findById(cartItemDto.getItemId()).orElseThrow(ExceptionInInitializerError::new);
        // 장바구니에 담을 상품 엔티티 조회

        Member member = memberRepository.findByEmail(email);
        // 현재 로그인한 회원 엔티티 조회

        Cart cart = cartRepository.findByMemberId(member.getId());
        // 현재 로그인한 회원의 장바구니 엔티티 조회

        if (cart == null) {
            // 상품을 처음으로 조회 장바구니에 담았을 경우 회원 장바구니 엔티티 생성해주기

            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());
        // 현재 상품이 장바구니에 이미 있는지 조회하기

        if (savedCartItem != null) {
            savedCartItem.addCount(cartItemDto.getCount());// 장바구니에 이미 있으면 기존 수량에 담는 수량 만큼 더해주기

            return savedCartItem.getId();

        } else {

            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDto.getCount());
            // 장바구니 엔티티, 상품 엔티티, 장부기네 담을 수량 이용해서 cartitem엔티티 엔티티 생성

            cartItemRepository.save(cartItem);// 장바구니에 들어갈 상품 저장

            return cartItem.getId();
        }
    }

    @Transactional(readOnly = true)
    public List<CartDetailDto> getCartList(String email) {

        List<CartDetailDto> cartDetailDtoList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email);

        Cart cart = cartRepository.findByMemberId(member.getId());
        // 로그인한 회원의 엔티티 조회

        if (cart == null) {// 장바구니 상품을 하나도 안담을 경우 장바구니 엔티티가 없으니 빈리스트로 반환
            return cartDetailDtoList;
        }

        cartDetailDtoList = cartItemRepository.findCartDetailDtoList(cart.getId());
        // 장바구니에 담아있는 상품 조회

        return cartDetailDtoList;
    }


    @Transactional(readOnly = true)
    public boolean validateCartItem(Long cartItemId, String email){  // 351 추가
        Member curMember = memberRepository.findByEmail(email);  // 현재 로그인한 사용자
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(EntityNotFoundException::new);
        Member savedMember = cartItem.getCart().getMember();  // 장바구니 상품을 저장한 회원 조회

        if(!StringUtils.equals(curMember.getEmail(), savedMember.getEmail())){  // 현재 로그인한 회원과 장바구니 상품을 저장한 회원이 다를 경우
            return false;
        }

        return true;
    }// 현재 로그인한 회원과 장바구니 상품을 저장한 회원이 다르면 false, 같으면 true 반환


    public void updateCartItemCount(Long cartItemId, int count) {
        // 장바구니 상품 수량을 업데이트 하는 메서드

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        cartItem.updateCount(count);
    }

    public void deleteCartItem(Long cartItemId) {

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);

        cartItemRepository.delete(cartItem);
    }

    public Long orderCartItem(List<CartOrderDto> cartOrderDtoList, String email) {
        log.info("데이터 : " + cartOrderDtoList.toString());
        List<OrderDto> orderDtoList = new ArrayList<>();

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {// 장바구니 페이지에서 전달받은 주문 상품 번호 이용해서 주문 로직으로 전달한 orderDto 객체 만든다.

            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            log.info("디티오 : " + cartOrderDto);
            OrderDto orderDto = new OrderDto();
            orderDto.setItemId(cartItem.getItem().getId());
            orderDto.setCount(cartItem.getCount());
            orderDtoList.add(orderDto);
        }

        Long orderId = orderService.orders(orderDtoList, email);// 장바구니에 담은 상품을 주문 하도록 주문 로직 호출

        for (CartOrderDto cartOrderDto : cartOrderDtoList) {
            CartItem cartItem = cartItemRepository.findById(cartOrderDto.getCartItemId()).orElseThrow(EntityNotFoundException::new);
            cartItemRepository.delete(cartItem);
            // 주문한 상품들을 주문했으니깐 장바구니에서는 제거
        }
        return orderId;

    }
}