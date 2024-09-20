package com.example.reward_monitoring.general;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class MainController {

    @Autowired
    private MemberRepository memberRepository;

    @GetMapping({ "", "/" })
    public String quizList(HttpSession session){

        return "actLogout";
    }

    @Operation(summary = "본인 회원정보 조회", description = "로그인한 유저의 회원정보를 가져옵니다.")
    @GetMapping("/Profile/myProfileWrite")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "회원정보 조회 성공"),
            @ApiResponse(responseCode = "401", description = "세션이 없거나 만료됨"),
            @ApiResponse(responseCode = "404", description = "조회 실패(세션은 있지만 세션정보가 DB에 없음)")
    })
    public String myProfile(HttpSession session, Model model) {

        Member sessionMember = (Member) session.getAttribute("member");
        if (sessionMember == null) {
            return "redirect:/login"; // 세션이 없으면 로그인 페이지로 리다이렉트
        } // 세션 만료

        Member member = memberRepository.findById(sessionMember.getId());
        if (member == null) {
            return "error/404"; // 데이터가 없을 때 404 페이지로 이동
        }

        model.addAttribute("member", member); // 조회한 회원 정보를 뷰에 전달
        return "myProfileWrite"; // myProfileWrite 뷰로 이동
    }
}
