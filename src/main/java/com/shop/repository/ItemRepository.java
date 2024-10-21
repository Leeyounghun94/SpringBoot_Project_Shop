package com.shop.repository;

import com.shop.entity.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ItemRepository extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {
    // ItemRepositoryCustom -> Querydsl로 구현한 페이지를 불러온다.

    List<Item> findByItemName(String itemName);//  itemNm을 이용해 아이템을 리스트와 찾아와

    List<Item> findByItemNameOrItemDetail(String itemName, String itemDetail);  // 상품명과 상세설명을 or조건을 이용하여 조회하는 메서드

    List<Item> findByPriceLessThan(Integer price);  // 파라미터로 넘어온 price 변수보다 값이 작은 데이터 조회 메서드

    List<Item> findByPriceLessThanOrderByPriceDesc(Integer price);// price 값보다 작거나 같은 아이템을 내림차순으로 정렬하고 아이템을 리스트와 찾아와

    @Query("select i from Item i where i.itemDetail like" +" %:itemDetail% order by i.price desc")
    List<Item> findByItemDetail(@Param("itemDetail") String itemDetail);
    // 엔티티 : 파라미터로 itemDetail값을 맞아 like로 찾아오고 가격순으로 내림차순하여 검색해와

    @Query(value="select * from item i where i.item_detail like " +
            "%:itemDetail% order by i.price desc", nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail") String itemDetail);
    // nativeQuery = true -> JPA가 아니라 엔티티를 사용하지 않음 -> 순수한 쿼리문
}
