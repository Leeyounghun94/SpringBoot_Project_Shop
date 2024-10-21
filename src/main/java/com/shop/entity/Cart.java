package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Entity
@Getter
@Setter
@ToString
@Table(name = "cart")
public class Cart extends  BaseEntity{


    @Id
    @Column(name = "cart_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)   //회원 엔티티하고 일대일로 매핑을 한다.
    @JoinColumn(name = "member_id") // 매핑할 외래키를 지정한다.
    private Member member;

    /* Hibernate:
        create table cart (
            cart_id bigint not null,
            member_id bigint,
            primary key (cart_id)
        ) engine=InnoDB
     카트 테이블 생성

     Hibernate:
    alter table if exists cart
       add constraint FKix170nytunweovf2v9137mx2o
       foreign key (member_id)
       references member (member_id)

     카트테이블에 외래키 추가하기.
     */

    public static Cart createCart(Member member) {
        // 회원 엔티티를 파라미터로 받는 장바구니 엔티티 생성

        Cart cart = new Cart();

        cart.setMember(member);

        return cart;
    }
}
