package com.shop.service;


import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemImgDto;
import com.shop.dto.ItemSearchDto;
import com.shop.dto.MainItemDto;
import com.shop.entity.Item;
import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ItemService {

    // 상품을 등록하는 서비스 객체

    //필드
    private final ItemRepository itemRepository;

    private final ItemImgRepository itemImgRepository;

    private final ItemImgService itemImgService;

    public Long saveItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        //상품 등록하기
        Item item = itemFormDto.createItem();   // 상품 등록 폼에서 받은 데이터를 이용하여 객체 생성
        itemRepository.save(item);// 상품 데이터 저장

        // 이미지 등록하기
        for (int i = 0; i < itemImgFileList.size(); i++) {
            ItemImg itemImg = new ItemImg();
            itemImg.setItem(item);

            if (i == 0)     // 첫 이미지 경우 값을 Y 지정, 나머지는 N 으로 설정
                itemImg.setRepimgYn("Y");
             else
                itemImg.setRepimgYn("N");

                itemImgService.saveItemImg(itemImg, itemImgFileList.get(i));    // 상품 이미지 정보 저장
            }

            return item.getId();
        }


        @Transactional(readOnly = true)// 상품데이터 읽어오는 트랜젝션 적용, Jpa가 변경감지 수행하지 않아서 서능 향상
        public ItemFormDto getItemDtl(Long itemId) {
        // 해당 상품 이미지 조회, 등록순으로 가져오기 위해 상품 이미지 오름차순으로 가져온다

            List<ItemImg> itemImgLiST = itemImgRepository.findByItemIdOrderByIdAsc(itemId);
            // 해당 상품 이미지 조회, 등록순으로 가져오기 위해 상품 이미지 오름차순으로 가져온다

            List<ItemImgDto> itemImgDtoList = new ArrayList<>();

            for (ItemImg itemImg : itemImgLiST) {
                // 조회한 아이템 이미지 엔티티를 DTO 만들어서 넣기

                ItemImgDto itemImgDto = ItemImgDto.of(itemImg);
                itemImgDtoList.add(itemImgDto);
            }

            Item item = itemRepository.findById(itemId).orElseThrow(EntityNotFoundException::new);
            // 상품 아이디 통해 상품 엔티티 조회, 존재하지 않을때 입센션 발동

            ItemFormDto itemFormDto = ItemFormDto.of(item);
            itemFormDto.setItemImgDtoList(itemImgDtoList);

            return itemFormDto;
        }


        public Long updateItem(ItemFormDto itemFormDto, List<MultipartFile> itemImgFileList) throws Exception {

        // 상품 수정하기

            Item item = itemRepository.findById(itemFormDto.getId()).orElseThrow(EntityNotFoundException::new);
            // 화면에서 전달받은 상품 아이디 이용하여 엔티티 조회하기

            item.updateItem(itemFormDto);
            // 화면에서 전달 받은 itemFormDto 통해 엔티티 업데이트 하기

            List<Long> itemImgIds = itemFormDto.getItemImgIds();
            // 상품 이미지 아이디 리스트 조회

            // 이미지 등록하기
            for (int i = 0; i<itemImgFileList.size(); i++ ) {
                itemImgService.updateItemImg(itemImgIds.get(i), itemImgFileList.get(i));
                // 이미지 업데이트 하기 위해서 updateItemImg메서드에 상품 이미지 아이디 + 상품 이미지 파일 정보 파라미터 전달
            }

            return item.getId();
        }


        @Transactional(readOnly = true)// 데이터의 수정이 일어나지 않게 최적화 하기 위해 설정
        public Page<Item> getAdminItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // 상품조회 조건과 페이지 정보를 파라미터로 받아서 상품 데이터 조회


        return itemRepository.getAdminItemPage(itemSearchDto, pageable);
        }

        @Transactional(readOnly = true)
        public Page<MainItemDto> getMainItemPage(ItemSearchDto itemSearchDto, Pageable pageable) {
        // 메인페이지에 보여줄 상품 데이터를 조회 하는 메서드


        return  itemRepository.getMainItemPage(itemSearchDto, pageable);
        }

    }


