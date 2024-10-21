package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.CartItemDto;
import com.shop.entity.CartItem;
import com.shop.entity.Item;
import com.shop.entity.Member;
import com.shop.repository.CartItemRepository;
import com.shop.repository.ItemRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class CartServiceTest {

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    MemberRepository memberRepository ;

    @Autowired
    CartService cartService ;

    @Autowired
    CartItemRepository cartItemRepository ;

    public Item savedItem() {

        Item item = new Item() ;

        item.setItemName("상품 아님");
        item.setItemDetail("아니라고 몇번말해요");
        item.setPrice(80000);
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);

        return itemRepository.save(item);

    }

    public Member savedMember() {
        Member member = new Member();

        member.setEmail("kkw@kkw.com");
        return memberRepository.save(member);
    }

    @Test
    @DisplayName("장바구니 담기")
    public void addCart() {

        Item item = savedItem();
        Member member = savedMember();

        CartItemDto cartItemDto = new CartItemDto();
        cartItemDto.setCount(5);
        cartItemDto.setItemId(item.getId());
        // 장바구니에 담을 수량 상품을 dto에다가 셋팅

        Long cartItemId = cartService.addCart(cartItemDto, member.getEmail());
        // 장바구니 담는 로직 결과를 생성된 장바구니 상품 아이디를 cartItemid 변수에 저장

        CartItem cartItem = cartItemRepository.findById(cartItemId).orElseThrow(EntityNotFoundException::new);
        // 장바구니 상품 아이디 이용하여 저장된 상품 아이디가 같다면 통과~

        assertEquals(item.getId(), cartItem.getItem().getId()); // 상품 아이디와 장바구니 아이디가 같다면 통과~
        assertEquals(cartItemDto.getCount(), cartItem.getCount());// 장바구니에 담았던 수량과 저장된 수량과 같다면 통과~
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
        c1_0.cart_id,
        c1_0.created_by,
        c1_0.member_id,
        c1_0.modified_by,
        c1_0.reg_time,
        c1_0.update_time
    from
        cart c1_0
    left join
        member m1_0
            on m1_0.member_id=c1_0.member_id
    where
        m1_0.member_id=?
Hibernate:
    select
        next value for cart_seq
Hibernate:
    insert
    into
        cart
        (created_by, member_id, modified_by, reg_time, update_time, cart_id)
    values
        (?, ?, ?, ?, ?, ?)
Hibernate:
    select
        ci1_0.cart_item_id,
        ci1_0.cart_id,
        ci1_0.count,
        ci1_0.created_by,
        ci1_0.item_id,
        ci1_0.modified_by,
        ci1_0.reg_time,
        ci1_0.update_time
    from
        cart_item ci1_0
    left join
        cart c1_0
            on c1_0.cart_id=ci1_0.cart_id
    left join
        item i1_0
            on i1_0.item_id=ci1_0.item_id
    where
        c1_0.cart_id=?
        and i1_0.item_id=?
Hibernate:
    select
        next value for cart_item_seq

     */
}
