package com.shop.controller;

import com.shop.dto.OrderDto;
import com.shop.dto.OrderHistDto;
import com.shop.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;


import java.security.Principal;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
public class OrderController {


    // 주문 관련 요청들 처리하는 컨트롤러

    private final OrderService orderService;

    @PostMapping(value = "/order")
    public @ResponseBody ResponseEntity order (@RequestBody @Valid OrderDto orderDto, BindingResult bindingResult, Principal principal) {
        // 스프링에서 비동기 처리 할 때는 @RequestBody + @ResponseBody를 사용한다
        // @RequestBody -> http 요청의 본문 body에 담긴 내용을 자바 객체로 전달한다.
        // ResponseBody -> 자바 객체를 http 요청의 body 전달한다.

        if(bindingResult.hasErrors()){ // orderDto 객체에 데이터 바인딩 시 에러가 있는지 검사.
            StringBuilder sb = new StringBuilder();
            List<FieldError> fieldErrors = bindingResult.getFieldErrors();

            for (FieldError fieldError : fieldErrors) {
                sb.append(fieldError.getDefaultMessage());
            }

            return new ResponseEntity<String>(sb.toString(), HttpStatus.BAD_REQUEST); // 에러 정보를 ResponseEntity 객체에 담아 반환
        }

        String email = principal.getName(); // 로그인 유저의 정보를 얻어 이름을 알아옴(이메일)
        Long orderId;

        try {
            orderId = orderService.order(orderDto, email);  // 화면으로 넘어오는 주문 정보과 회원 이메일 정보를 이용하여 주문 로직을 호출
        } catch(Exception e){
            return new ResponseEntity<String>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<Long>(orderId, HttpStatus.OK); // 정상처리
    }


    @GetMapping(value = {"/orders", "/orders{page}"})
    public String orderHist(@PathVariable("page")Optional<Integer> page, Principal principal, Model model) {
        // 구매 이력을 조회할 수 있는 메서드

        Pageable pageable = PageRequest.of(page.isPresent() ? page.get() : 0, 4);
        // 한번에 가져올 주문의 개수는 총 4개로 설정

        Page<OrderHistDto> orderhistDtoList = orderService.getOrderList(principal.getName(), pageable);
        // 현재 로그인한 회원은 이메일과 페이징 객체를 파라미터 전달해서 화면에 전달한 주문 목록 데이터를 리턴값으로 받는다.

        model.addAttribute("orders", orderhistDtoList);
        model.addAttribute("page", pageable.getPageNumber());
        model.addAttribute("maxPage", 5);

        return "order/orderHist";
    }


    @PostMapping("/order/{orderId}/cancel")
    public  @ResponseBody ResponseEntity cancelOrder(@PathVariable("orderId") Long orderId, Principal principal) {
        // 주문 번호를 받아서 주문 취소 로직을 호출하는 메서드

        if (!orderService.validateOrder(orderId, principal.getName())) {
            // 자바 스크립트 에서 취소할 주문 번호는 조작이 가능하므로 다른 사람이 주문을 취소하지 못하도록 주문 취소 권한을 검사해야 함
            return new ResponseEntity<String>("주문 취소 권한이 없습니다.", HttpStatus.FORBIDDEN);
        }

        orderService.cancelOrder(orderId);
        // 주문 취소 로직을 호출
        return new ResponseEntity<Long>(orderId, HttpStatus.OK);
    }

}
