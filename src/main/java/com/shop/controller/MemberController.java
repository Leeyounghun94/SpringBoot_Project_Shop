package com.shop.controller;

import com.shop.dto.MemberFormDto;
import com.shop.entity.Member;
import com.shop.service.MemberService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@RequestMapping("members")
@Controller
@RequiredArgsConstructor
@Log4j2
public class MemberController {

    private final MemberService memberService;
    private final PasswordEncoder passwordEncoder;

    @GetMapping(value = "/new")
    public String memberForm(Model model) { // 회원가입 페이지 이동하는 메서드

        model.addAttribute("memberFormDto", new MemberFormDto());

        return "member/memberForm";
    }


    @PostMapping(value = "/new")
    public String newMember(@Valid MemberFormDto memberFormDto, BindingResult bindingResult, Model model) {
        // 검증하려면 객체 앞에 @vild 붙이고 파라미터로 bindingResult 추가 후 결과를 담아준다.
        log.info(memberFormDto.toString());

        if (bindingResult.hasErrors()) {//에러가 있다면 회원 가입 페이지 이동
            return "member/memberForm";
        }

        try {
            Member member = Member.createMember(memberFormDto, passwordEncoder);
            memberService.saveMember(member);
        } catch (IllegalStateException e) {
            model.addAttribute("errorMessage", e.getMessage()); //  중복 회원 발생 시 에러메세지 뷰로 전달..
            return "member/memberForm";
        }

        return "redirect:/";
    }


    @GetMapping(value = "/login")
    public String loginMember(){

        return "member/memberLoginForm";
    }



    @GetMapping(value = "/login/error")
    public String loginError(Model model) {
        model.addAttribute("loginErrorMsg", "아이디와 비밀번호를 확인해주세요.");
        return "member/memberLoginForm";
    }
}
