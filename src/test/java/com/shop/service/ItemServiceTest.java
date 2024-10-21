package com.shop.service;

import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mock.web.MockMultipartFile;

import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@Transactional
@TestPropertySource(locations = "classpath:application-test.properties")
public class ItemServiceTest {

    @Autowired
    ItemService itemService;

    @Autowired
    ItemRepository itemRepository;

    @Autowired
    ItemImgRepository itemImgRepository;

    List<MultipartFile> createMultiPartFiles() throws Exception{// MockMultipart클래스 이용해서 가짜 multipartfile 리스트 반환 메서드

        List<MultipartFile> multipartFileList = new ArrayList<>();

        for (int i=0; i<5 ; i++) {

            String path = "C:/shop/item/";
            String imageName = "image" + i + ".jpg" ;

            MockMultipartFile multipartFile = new MockMultipartFile(path, imageName, "image/jpg", new byte[]{1,2,3,4});

            multipartFileList.add(multipartFile);
        }

        return multipartFileList;
    }

    @Test
    @DisplayName("상품 등록 테스트")
    //@WithMockUser(username = "admin", roles = "ADMIN")
    void saveItem() throws Exception {  //

        ItemFormDto itemFormDto = new ItemFormDto();    // 상품 등록 화면에서 입력 받는 상품 데이터 세팅
        itemFormDto.setItemName("상품 테스트1");
        itemFormDto.setItemSellStatus(ItemSellStatus.SELL);
        itemFormDto.setItemDetail("이것은 상품이 아닙니다 ㅋ");
        itemFormDto.setPrice(70000);
        itemFormDto.setStockNumber(100);

        List<MultipartFile> multipartFileList = createMultiPartFiles();
        Long itemId = itemService.saveItem(itemFormDto, multipartFileList); // 상품데이터하고 이미지 정보를 파라미터로 넘겨서 저장하고 저장된 값을 반환하여 리턴

        List<ItemImg> itemImgList = itemImgRepository.findByItemIdOrderByIdAsc(itemId);

        Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);

        assertEquals(itemFormDto.getItemName(), item.getItemName());    // 입력한 데이터와 실제로 저장된 데이터가 같은지 확인
        assertEquals(itemFormDto.getItemSellStatus(), item.getItemSellStatus());
        assertEquals(itemFormDto.getItemDetail(), item.getItemDetail());
        assertEquals(itemFormDto.getPrice(), item.getPrice());
        assertEquals(itemFormDto.getStockNumber(), item.getStockNumber());
        assertEquals(multipartFileList.get(0).getOriginalFilename(), itemImgList.get(0).getOriImgName());// 상품 이미지는 첫번째 파일의 원본 파일 이름이 같은지 확인

        /*
        Hibernate:
    insert
    into
        item_img
        (created_by, img_name, img_url, item_id, modified_by, ori_img_name, reg_time, repimg_yn, update_time, item_img_id)
    values
        (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
Hibernate:
    select
        ii1_0.item_img_id,
        ii1_0.created_by,
        ii1_0.img_name,
        ii1_0.img_url,
        ii1_0.item_id,
        ii1_0.modified_by,
        ii1_0.ori_img_name,
        ii1_0.reg_time,
        ii1_0.repimg_yn,
        ii1_0.update_time
    from
        item_img ii1_0
    where
        ii1_0.item_id=?
    order by
        ii1_0.item_img_id
OpenJDK 64-Bit Server VM warning: Sharing is only supported for boot loader classes because bootstrap classpath has been appended
2024-10-17T20:27:48.101+09:00  INFO 9452 --- [shop] [ionShutdownHook] j.LocalContainerEntityManagerFactoryBean : Closing JPA EntityManagerFactory for persistence unit 'default'
2024-10-17T20:27:48.103+09:00  INFO 9452 --- [shop] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown initiated...
2024-10-17T20:27:48.109+09:00  INFO 9452 --- [shop] [ionShutdownHook] com.zaxxer.hikari.HikariDataSource       : HikariPool-1 - Shutdown completed.
> Task :test
BUILD SUCCESSFUL in 19s
5 actionable tasks: 5 executed
오후 8:27:48: Execution finished ':test --tests "com.shop.service.ItemServiceTest.saveItem"'.

         */
    }


}
