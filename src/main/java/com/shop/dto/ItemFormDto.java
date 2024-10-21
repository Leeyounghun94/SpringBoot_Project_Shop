package com.shop.dto;

import com.shop.constant.ItemSellStatus;
import com.shop.entity.Item;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class ItemFormDto {

    private Long id;

    @NotBlank(message = "상품명은 필수 입력값 입니다.")
    private String itemName ;

    @NotNull(message = "가격은 필수 입력값 입니다.")
    private Integer price ;

    @NotBlank(message = "상세설명은 필수 입력값 입니다.")
    private String itemDetail ;

    @NotNull(message = "재고는 필수 입력값 입니다.")
    private Integer stockNumber ;

    private ItemSellStatus itemSellStatus;

    private List<ItemImgDto> itemImgDtoList = new ArrayList<>();    // 상품 저장 후 수정할때 이미지 정보 저장하는 리스트

    private List<Long> itemImgIds = new ArrayList<>();  // 상품의 이미지 아이디 저장 리스트 , 이미지 저장하지 않아서 아무값 들어있지 않고 수정시에만 이미지 아이디 담아 둘 용도로 쓰임

    private static ModelMapper modelMapper = new ModelMapper();

    public Item createItem() {

        return modelMapper.map(this, Item.class);
    }

    public static ItemFormDto of(Item item) {// 위에와 이 메서드는 modelMapper 이용하여 엔티티 객체와 DTO객체 간의 데이터 복사해서 복사한 객체를 반환해준다.

        return modelMapper.map(item, ItemFormDto.class);
    }
}
