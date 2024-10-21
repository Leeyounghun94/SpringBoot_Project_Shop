package com.shop.repository;

import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ItemRepositoryCustom {

    // 아이템 레포지토리 사용자 정의

    Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    // 상품 조회 조건을 담는 ItemSearchDto + 페이징 정보를 담는 Pageable 객체를 파라미터로 받는 메서드


    Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable);
    // 메인 페이지에 보여줄 상품 리스트를 가져오는 메서드
}
