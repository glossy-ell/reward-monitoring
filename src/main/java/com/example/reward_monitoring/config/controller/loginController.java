package com.example.reward_monitoring.config.controller;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import com.example.reward_monitoring.general.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.SessionAttributes;

@Controller
@Tag(name = "Member", description = "로그인 , 세션,회원정보 컨트롤러 ")
@SessionAttributes("member")
public class loginController {

    @Autowired
    private MemberRepository memberRepository;
    @Autowired
    private MemberService memberService;
    @ModelAttribute("member")
    public Member setUpUserForm() {
        return new Member();
    }

    @Operation(summary = "회원정보", description = "로그인한 유저의 회원정보를 가져옵니다.")
    @GetMapping("/Profile/myProfileWrite")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 조회 성공"),
            @ApiResponse(responseCode = "404", description = "권한없음(세션이 없거나 만료됨)")
    })
    public ResponseEntity<Member> myProfile(HttpSession session){

        Member member= (Member) session.getAttribute("member");
        if(member == null){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Member compare =memberRepository.findById(member.getId());
        if (compare == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(compare);
    }

    @Operation(summary = "로그아웃", description = "로그아웃합니다. 세션은 더이상 유효하지않습니다.")
    @GetMapping("/logout")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "로그아웃 성공"),
    })
    public ResponseEntity<Void>logout(HttpSession session) {
        session.invalidate();
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
