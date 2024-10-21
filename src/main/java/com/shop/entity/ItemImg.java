package com.shop.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "item_img")
@Getter
@Setter
public class ItemImg extends BaseEntity {


    @Id
    @Column(name = "item_img_id")
    @GeneratedValue(strategy = GenerationType.AUTO)
    private  Long id;

    private String imgName ;    // 이미지 파일명

    private String oriImgName ;     // 원본 이미지 파일명

    private String imgUrl ; // 이미지 조회 경로

    private String repimgYn ; // 대표 이미지 여부

    @ManyToOne(fetch = FetchType.LAZY)  // 아이템과 다대일 단방향으로 매핑하기, 지연로딩 설정해서 정보가 필요하면 데이터 조회
    @JoinColumn(name = "item_id")
    private Item item ;

    public void updateItemImg(String oriImgName, String imgName, String imgUrl) {// 각종 파라미터를 받아서 이미지 정보를 업데이트 메서드
        this.imgName = imgName;
        this.oriImgName = oriImgName;
        this.imgUrl = imgUrl ;

    }
}
