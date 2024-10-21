package com.shop.service;

import com.shop.entity.ItemImg;
import com.shop.repository.ItemImgRepository;
import com.shop.repository.ItemRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.util.StringUtils;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemImgService {

    @Value("${itemImgLocation}")
    // application-properties에 등록된 itemImgLocation 불러와서 itemImgLocation 변수에 넣는다.
    private String itemImgLocation ;

    private final ItemImgRepository itemImgRepository ;
    private final FileService fileService;

    public void saveItemImg(ItemImg itemImg, MultipartFile itemImgFile) throws Exception {

        String oriImgName = itemImgFile.getOriginalFilename();
        String imgName = "";
        String imgUrl = "";

        // 파일 업로드
        if (!StringUtils.isEmpty(oriImgName)) {

            imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes()); // 상품 이미지 등록했다면 저장할 경로, 파일 이름, 바이트 배열을 파라미터에 따라 메서드 호출
            imgUrl = "/images/item/" + imgName;// 저장한 이미지 상품을 불러올 경로 설정,
        }

        // 상품 이미지 정보 저장
        itemImg.updateItemImg(oriImgName, imgName, imgUrl); // 입력받은 상품 이미지 정보 저장(업로드한 이미지 원래이름, 실제로 등록한 이미지이름, 이미지파일 불러올 경로
        itemImgRepository.save(itemImg);//레포지토리에 저장
    }

    public void updateItemImg(Long itemImgId, MultipartFile itemImgFile) throws Exception{

        if (!itemImgFile.isEmpty()) {
            ItemImg savedItemImg = itemImgRepository.findById(itemImgId).orElseThrow(EntityNotFoundException::new);

            //기존 이미지 파일 사제
            if (!StringUtils.isEmpty((savedItemImg.getImgName()))) {
                // 기존에 이미지 파일 있을 경우 삭제 하기

                fileService.deleteFile(itemImgLocation+"/"+savedItemImg.getImgName());

            }

            String oriImgName = itemImgFile.getOriginalFilename();
            String imgName = fileService.uploadFile(itemImgLocation, oriImgName, itemImgFile.getBytes());
            // 업데이트한 상품 이미지 파일 업로드

            String imgUrl = "/images/item/" + imgName ;
            savedItemImg.updateItemImg(oriImgName, imgName, imgUrl);
            //변경된 내용으로 상품 업데이트, 중요한것은 레포지토리.save를 하지 않는다는 점!
            // 트랜젝션이 끝날때 update 쿼리 실행되기 때문에 엔티티가 영속 상태이여야한다.

        }
    }

}
