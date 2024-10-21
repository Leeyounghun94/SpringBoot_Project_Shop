package com.shop.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "cart_item")
public class CartItem extends BaseEntity{


    @Id
    @GeneratedValue
    @Column(name = "cart_item_id")
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;  // 하나의 장바구니에 여러 상품을 담을 수 있으므로 @MANYONE 이용하여 다대일 관계

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;  // 담을 상품의 정보를 알아야해서 엔티티를 매핑해준다. 이것도 여러개 상품 담을 수 있어서 다대일 관계 해준다.

    private int count ; // 같은 상품을 몇개 다음지 저장.


    /* Hibernate:
        create table cart_item (
            cart_item_id bigint not null,
            count integer not null,
            cart_id bigint,
            item_id bigint,
            primary key (cart_item_id)
        ) engine=InnoDB
        cart_item 테이블 생성


        Hibernate:
    alter table if exists cart_item
       add constraint FK1uobyhgl1wvgt1jpccia8xxs3
       foreign key (cart_id)
       references cart (cart_id)
Hibernate:
    alter table if exists cart_item
       add constraint FKdljf497fwm1f8eb1h8t6n50u9
       foreign key (item_id)
       references item (item_id)

       외래키 추가.
     */

    public static CartItem createCartItem(Cart cart, Item item, int count) {

        CartItem cartItem = new CartItem();

        cartItem.setCart(cart);
        cartItem.setItem(item);
        cartItem.setCount(count);

        return cartItem;
    }

    public void addCount(int count) {
        // 장바구니에 기존에 담겨있는 상품인데 해당 상품을 추가로 장바구니 담았을때 기존 수량에 현재 담는 수량을 더해주는 메서드
        this.count += count ;
    }

    public void updateCount(int count) {

        this.count = count;
    }
}
