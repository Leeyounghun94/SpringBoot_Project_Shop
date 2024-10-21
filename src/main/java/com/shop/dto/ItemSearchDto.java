package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ItemSearchDto {

    // 상품 데이터 조회 시 상품 조회 조건을 가지고 있는 객체 !

    private String searchDateType, searchBy ;// 현재시간 + 상품 등록일 비교해서 데이터 조회 / 조회할때 어떤 유형을 으로 조회할지? 네임? 아이디?

    private ItemSellStatus searchSellStatus ;// 판매 상태 기준으로 데이터 조회

    private String searchQuery = "";    // 조회할 검색어 저장 변수, 상품명이면 상품명 기준으로 검색, 아이디생성 이면 상품 등록자 아이디 기준
}
