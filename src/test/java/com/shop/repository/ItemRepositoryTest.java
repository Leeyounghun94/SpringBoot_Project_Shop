package com.shop.repository;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class ItemRepositoryTest {

    @Autowired
    ItemRepository itemRepository;

    @Test
    @DisplayName("상품 저장 테스트")
    public void createItemTest() {
        Item item = new Item();
        item.setItemName("테스트 상품");
        item.setPrice(1000);
        item.setItemDetail("상품 상세설명");
        item.setItemSellStatus(ItemSellStatus.SELL);
        item.setStockNumber(100);
        item.setRegTime(LocalDateTime.now());
        item.setUpdateTime(LocalDateTime.now());

        Item savedItem = itemRepository.save(item);

        System.out.println(savedItem.toString());
    }
    /*  Hibernate:
    insert
    into
        item
        (item_detail, item_name, item_sell_status, price, reg_time, stock_number, update_time, item_id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?)
     */

    public void createItemList() {
        for (int i = 1; i <= 10; i++) {
            Item item = new Item();
            item.setItemName("테스트 상품" + i);
            item.setPrice(10000 + i);
            item.setItemDetail("테스트 상품 상세 설명" + i);
            item.setItemSellStatus(ItemSellStatus.SELL);
            item.setStockNumber(100);
            item.setRegTime(LocalDateTime.now());
            item.setUpdateTime(LocalDateTime.now());
            Item savedItem = itemRepository.save(item);
        }
    }

    @Test
    @DisplayName("상품명 조회 테스트")
    public void findByItemNameTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemName("테스트 상품1");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }
    /* Hibernate:
    select
        i1_0.item_id,
        i1_0.item_detail,
        i1_0.item_name,
        i1_0.item_sell_status,
        i1_0.price,
        i1_0.reg_time,
        i1_0.stock_number,
        i1_0.update_time
    from
        item i1_0
    where
        i1_0.item_name=?
        Item(id=1, itemName=테스트 상품1, price=10001, stockNumber=100, itemDetail=테스트 상품 상세 설명1, itemSellStatus=SELL,
        regTime=2024-10-11T19:54:11.679859, updateTime=2024-10-11T19:54:11.679859)
     */

    @Test
    @DisplayName("상품명, 상세설명 or 테스트")
    public void findByItemNameOrItemDetailTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemNameOrItemDetail("테스트 상품1", "상품 상세설명5");
        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }

    @Test
    @DisplayName("가격 Less than 테스트")
    public void findByPriceLessThenTest() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByPriceLessThan(10006);

        for (Item item : itemList) {
            System.out.println(item.toString());
        }
    }
    /* Item(id=1, itemName=테스트 상품1, price=10001, stockNumber=100, itemDetail=테스트 상품 상세 설명1, itemSellStatus=SELL, regTime=2024-10-11T20:03:43.697851, updateTime=2024-10-11T20:03:43.697851)
        Item(id=2, itemName=테스트 상품2, price=10002, stockNumber=100, itemDetail=테스트 상품 상세 설명2, itemSellStatus=SELL, regTime=2024-10-11T20:03:43.903978, updateTime=2024-10-11T20:03:43.903978)
        Item(id=3, itemName=테스트 상품3, price=10003, stockNumber=100, itemDetail=테스트 상품 상세 설명3, itemSellStatus=SELL, regTime=2024-10-11T20:03:43.906963, updateTime=2024-10-11T20:03:43.906963)
}*/

    @Test
    @DisplayName("@쿼리 이용한 상품 조회 테스트")
    public void findItemdetailTEST() {
        this.createItemList();
        List<Item> itemList = itemRepository.findByItemDetail("상품 상세 설명");

        for (Item item : itemList){
            System.out.println(item.toString());
        }
    }
}