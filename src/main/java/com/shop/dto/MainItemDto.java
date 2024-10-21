package com.shop.dto;


import com.querydsl.core.annotations.QueryProjection;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class MainItemDto {

    private Long id;

    private String itemName, itemDetail, imgUrl ;

    private Integer price ;

    @QueryProjection
    public MainItemDto(Long id, String itemName, String itemDetail, String imgUrl, Integer price) {
        //@QueryProjection 선언하여 QuerydSL로 결과 조회 시 MainItemDto 객체로 바로 받아온다

        this.id = id;
        this.itemName = itemName;
        this.itemDetail = itemDetail;
        this.imgUrl = imgUrl;
        this.price = price;
    }
}
