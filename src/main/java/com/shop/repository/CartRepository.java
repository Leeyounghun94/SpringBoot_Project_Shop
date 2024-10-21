package com.shop.repository;

import com.shop.entity.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Cart findByMemberId(Long memberId); // 현재 로그인한 회원의 카트 엔티티 찾기 위한 쿼리 메서드

}
