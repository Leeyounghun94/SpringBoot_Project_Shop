package com.shop.repository;

import com.shop.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Member findByEmail(String email); // 회원가입 할 때 중복된 값이 있는지 검사하기 위해 쿼리 메서드 작성
}
