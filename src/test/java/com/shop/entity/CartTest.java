package com.shop.entity;


import com.shop.dto.MemberFormDto;
import com.shop.repository.CartRepository;
import com.shop.repository.MemberRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
class CartTest {

    @Autowired
    CartRepository cartRepository;

    @Autowired
    MemberRepository memberRepository;

    @Autowired
    PasswordEncoder passwordEncoder;

    @PersistenceContext
    EntityManager em;


    public Member createMember() {

        MemberFormDto memberFormDto = new MemberFormDto();

        memberFormDto.setAddress("수원시 장안구");
        memberFormDto.setName("김기원");
        memberFormDto.setPassword("1234");
        memberFormDto.setEmail("kkk@kkk.com");

        Member member = Member.createMember(memberFormDto, passwordEncoder);

        return Member.createMember(memberFormDto, passwordEncoder);
    }


    @Test
    @DisplayName("장바구니 회원 엔티티 매핑 테스트")
    public void findCartAndMemberTest() {

        Member member = createMember();
        memberRepository.save(member);

        Cart cart = new Cart();
        cart.setMember(member);
        cartRepository.save(cart);

        em.flush(); // 트랜젝션 끝날때 flush 호출해서 db에 반영
        em.clear(); // 엔티티 조회 후 없으면 db조회하고 장바구니 엔티티 가져올때 멤버도 가져오는지 확인

        Cart savedCart = cartRepository.findById(cart.getId()).orElseThrow(EntityNotFoundException::new);
        // 저장된 장보기 엔티티 조회

        Assertions.assertEquals(savedCart.getMember().getId(), member.getId());

        /* Hibernate:
            select
                c1_0.cart_id,
                m1_0.member_id,
                m1_0.address,
                m1_0.email,
                m1_0.name,
                m1_0.password,
                m1_0.role
            from
                cart c1_0
            left join
                member m1_0
                    on m1_0.member_id=c1_0.member_id
            where
                c1_0.cart_id=?
         */

    }
}
