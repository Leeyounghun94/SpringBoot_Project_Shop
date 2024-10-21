package com.shop.controller;


import com.shop.constant.ItemSellStatus;
import com.shop.dto.ItemFormDto;
import com.shop.dto.ItemSearchDto;
import com.shop.entity.Item;
import com.shop.service.ItemService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Log4j2
public class itemController {
    private final ItemService itemService;

    @GetMapping(value = "/admin/item/new")
    public String itemForm(Model model){
        model.addAttribute("itemFormDto", new ItemFormDto());
        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/new")
    public String itemNew(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                          Model model, @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList){

        if(bindingResult.hasErrors()){    // 상품 등록 시 필수 값 없으면 다시 상품 등록페이지로 가라~
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            // 상품 첫 등록 시 이미지 없으면 에레메세지 + 상품 등록 페이지로 이동

            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.saveItem(itemFormDto, itemImgFileList); // 상품 저장 로직, 상품 정보와 이미지 정보를 리스트로 넘긴다.
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 등록 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";    // 정상적으로 처리되면 메인페이지로 이동
    }

    @GetMapping(value = "/admin/item/{itemId}")
    public String itemDtl(@PathVariable("itemId") Long itemId, Model model){

        try {
            ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
            // 조회한 상품 데이터를 모델에 담아서 뷰로 전달

            model.addAttribute("itemFormDto", itemFormDto);

        } catch(EntityNotFoundException e){// 상품 엔티티가 존재하지 않을 경우 에러메세지 전달, 상품 등록 페이지로 이동

            model.addAttribute("errorMessage", "존재하지 않는 상품 입니다.");
            model.addAttribute("itemFormDto", new ItemFormDto());
            return "item/itemForm";
        }

        return "item/itemForm";
    }

    @PostMapping(value = "/admin/item/{itemId}")
    public String itemUpdate(@Valid ItemFormDto itemFormDto, BindingResult bindingResult,
                             @RequestParam("itemImgFile") List<MultipartFile> itemImgFileList, Model model){
        if(bindingResult.hasErrors()){
            return "item/itemForm";
        }

        if(itemImgFileList.get(0).isEmpty() && itemFormDto.getId() == null){
            model.addAttribute("errorMessage", "첫번째 상품 이미지는 필수 입력 값 입니다.");
            return "item/itemForm";
        }

        try {
            itemService.updateItem(itemFormDto, itemImgFileList);               // 상품 수정 로직 호출
        } catch (Exception e){
            model.addAttribute("errorMessage", "상품 수정 중 에러가 발생하였습니다.");
            return "item/itemForm";
        }

        return "redirect:/";
    }

    @GetMapping(value = {"/admin/items", "/admin/items/{page}"})
    // 상품 관리 경로에 들어갈 때 페이지 번호 없는 경우와 페이지 번호가 있는 경우 2가지를 매핑한다.

    public String itemManage(ItemSearchDto itemSearchDto, @PathVariable("page") Optional<Integer> page, Model model){

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 3);
        // 페이징 처리, 파라미터로 조회할 페이지, 한번에 가져올 데이터 수를 넣고, 경로에 페이지 번호가 있으면 해당 페이지 조회하고, 번호 없으면 0페이지 조회한다.

        Page<Item> items = itemService.getAdminItemPage(itemSearchDto, pageable);    // 조회조건, 페이징 정보를 파라미터로 넘겨서 객체반환

        model.addAttribute("items", items); // 조회한 상품 데이터, 페이징 정보를 뷰로 전달
        model.addAttribute("itemSearchDto", itemSearchDto); // 페이지 전환 시 기존 검색 조건 유지한채로 이동할 수 있게 뷰에 다시 전달
        model.addAttribute("maxPage", 5);   // 상품 관리 메뉴 하단의 최대 페이지 번호 개수 , 5로 설정했기 때문에 최대 5개 이동할 페이지 번호만 보여준다.

        return "item/itemMng";
    }

    @GetMapping(value = "/item/{itemId}")
    public String itemDtl(Model model, @PathVariable("itemId") Long itemId){
        // 상품 상세 페이지 이동하기.

        ItemFormDto itemFormDto = itemService.getItemDtl(itemId);
        log.info("아이디 : " +itemId);
        log.info("상태 : " + ItemSellStatus.SELL);
        model.addAttribute("item", itemFormDto);
        return "item/itemDtl";
    }
}
