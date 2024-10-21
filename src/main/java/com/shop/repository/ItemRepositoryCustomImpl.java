package com.shop.repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.Wildcard;
import com.querydsl.jpa.impl.JPAQueryFactory;
import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.dto.QMainItemDto;
import com.shop.entity.Item;
import com.shop.entity.QItem;
import com.shop.entity.QItemImg;
import jakarta.persistence.EntityManager;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.thymeleaf.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

public class ItemRepositoryCustomImpl implements ItemRepositoryCustom{
    //ItemRepositoryCustom 을 상속 받는다.

    private JPAQueryFactory queryFactory ;  // 동적으로 쿼리 생성하기 위해 JpaQueryFactory 사용

    public ItemRepositoryCustomImpl(EntityManager em) {
        // JpaQueryFactory 생성자로 EntityManager 객체를 넣어준다.

        this.queryFactory = new JPAQueryFactory(em);
    }

    private BooleanExpression searchSellStatusEq(ItemSellStatus searchSellStatus) {
        // 상품 판매 조건이 null인 경우 리턴시킨다. 결과값이 null이면 조건에서 해당 조건 무시되고 null이 아닌 판매중, 품절이면 해당 조건의 상품만 조회한다.

        return searchSellStatus == null ? null : QItem.item.itemSellStatus.eq(searchSellStatus);
        //
    }

    private BooleanExpression regDtsAfter(String searchDateType){
        // searchDateType에 따라 dateTime 값을 이전 시간의 값으로 세팅 후 해당 시간 이후로 등록된 상품으로만 조회한다.
        // 1분일 경우 시간값을 한달 전으로 세팅 해서 최근 한달 동안 등록된 상품만 조회하도록 반환한다.

        LocalDateTime dateTime = LocalDateTime.now();

        if (StringUtils.equals("all", searchDateType) || searchDateType == null) {

            return null;
        } else if (StringUtils.equals("1d", searchDateType)) {
            dateTime = dateTime.minusDays(1);

        } else if (StringUtils.equals("1w", searchDateType)) {
            dateTime = dateTime.minusWeeks(1);

        } else if (StringUtils.equals("1m", searchDateType)) {
            dateTime = dateTime.minusMonths(1);

        } else if (StringUtils.equals("6m", searchDateType)) {
            dateTime = dateTime.minusMonths(6);
        }

        return QItem.item.regTime.after(dateTime);

    }

    private BooleanExpression searchByLike(String searchBy, String searchQuery) {
        // searchBy값 따라 상품명에 검색어 포함하고 있는 상품, 상품 생산자 아이디에 검색어 포함하고 있는 상품을 조회하도록 조건값을 반환한다.

        if(StringUtils.equals("itemName", searchBy)){
            return QItem.item.itemName.like("%" + searchQuery + "%");
        } else if(StringUtils.equals("createdBy", searchBy)){
            return QItem.item.createdBy.like("%" + searchQuery + "%");
        }

        return null;

    }


    @Override
    public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        List<Item> content = queryFactory
                .selectFrom(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(),
                                itemSearchDto.getSearchQuery()))
                .orderBy(QItem.item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        /* 쿼리 팩토리를 이용해서 쿼리를 생성,
        selectFrom -> 상품 데이터 조회하기 위해 Qitem의 item 지정
        where -> BooleanExpression 반환하는 조건문이다.
        offset -> 데이터를 가지고 올 인덱스를 지정한다
        limit -> 한번에 가져올 최대 개수를 지정한다.
        fetchResults -> 리스트하고 전체 개수를 QueryResult 반환, 상품 데이터 리스트 조회와 상품 데이터 전체 개수를 조회하는 2번의 쿼리문이 실행된다.
         */

        long total = queryFactory.select(Wildcard.count).from(QItem.item)
                .where(regDtsAfter(itemSearchDto.getSearchDateType()),
                        searchSellStatusEq(itemSearchDto.getSearchSellStatus()),
                        searchByLike(itemSearchDto.getSearchBy(), itemSearchDto.getSearchQuery()))
                .fetchOne()
                ;

        return new PageImpl<>(content, pageable, total);
        // 조회한 데이터를 page클래스인 구현체로 리턴한다.
    }

    private BooleanExpression itemNameLike(String searchQuery) {
        //검색어가 null이 아니면 상품명에 해당 검색어가 포함된 상품을 조회하는 조건을 반환

        return StringUtils.isEmpty(searchQuery) ? null : QItem.item.itemName.like("%" + searchQuery + "%");

    }

    @Override
    public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {

        QItem item = QItem.item;
        QItemImg itemImg = QItemImg.itemImg;

        List<MainItemDto> content = queryFactory.select(
                        new QMainItemDto(
                                // 여기에다가 반환 값을 넣어주고 @QueryProjection을 사용하면 바로 DTO로 조회가 가능하고 엔티티 조회 하고 DTD로 반환 과정을 줄일 수 있음

                                item.id,
                                item.itemName,
                                item.itemDetail,
                                itemImg.imgUrl,
                                item.price)
                )
                .from(itemImg)
                .join(itemImg.item, item)// 이미지와 상품을 내부조인
                .where(itemImg.repimgYn.eq("Y"))// 이미지인 경우 대표 상품 이미지만 불러오기.
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .orderBy(item.id.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        long total = queryFactory
                .select(Wildcard.count)
                .from(itemImg)
                .join(itemImg.item, item)
                .where(itemImg.repimgYn.eq("Y"))
                .where(itemNameLike(itemSearchDto.getSearchQuery()))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);

    }
}


