package com.shop.dto;


import com.shop.entity.ItemImg;
import lombok.Getter;
import lombok.Setter;
import org.modelmapper.ModelMapper;

@Getter
@Setter
public class ItemImgDto {

    private  Long id;

    private String imgName ;    // 이미지 파일명

    private String oriImgName ;     // 원본 이미지 파일명

    private String imgUrl ; // 이미지 조회 경로

    private String repimgYn ; // 대표 이미지 여부

    private static ModelMapper modelMapper = new ModelMapper();

    public static ItemImgDto of(ItemImg itemImg) {

        return modelMapper.map(itemImg, ItemImgDto.class);
        // itemImg 엔티티 객체를 파라미터로 받아서 itemImg 객체의 자료형과 멤버 변수 같을때 ItemImgDto로 값 복사해서 반환한다.
        // static으로 선언했기 때문에 ItemImgDto 생성하지 않아도 호출할수 있다.
    }
}
