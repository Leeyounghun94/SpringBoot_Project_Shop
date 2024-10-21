package com.shop.entity;

import com.shop.constant.Role;
import com.shop.dto.MemberFormDto;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.crypto.password.PasswordEncoder;

@Entity
@Table(name = "member")
@Getter
@Setter
@ToString
public class Member extends BaseEntity{

    @Id
    @Column(name = "member_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id ;

    private String name ;

    @Column(unique = true)
    private String email ;  // 이메일 통해서 회원 구분하기 때문에 유니크 설정

    private String password ;

    private String address ;

    @Enumerated(EnumType.STRING)
    private Role role;  // Eunm을 엔티티 속성 지정, 순서가 바뀔수가 있기 때문에 String으로 저장

    public static Member createMember(MemberFormDto memberFormDto, PasswordEncoder passwordEncoder){
        Member member = new Member();
        member.setName(memberFormDto.getName());
        member.setEmail(memberFormDto.getEmail());
        member.setAddress(memberFormDto.getAddress());
        String password = passwordEncoder.encode(memberFormDto.getPassword());  // 패스워드 암호화 처리
        member.setPassword(password);
        member.setRole(Role.ADMIN);  //권한은 ADMIN
        return member;
    } // 회원 생성용 메서드 (dto와 암호화를 받아 Member 객체 리턴)

}
