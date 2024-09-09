package com.example.reward_monitoring.config;


import com.example.reward_monitoring.general.member.service.MemberService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private MemberService memberService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,Authentication authentication) {
        response.setStatus(HttpServletResponse.SC_OK);
        String memberId = authentication.getName(); // 사용자 ID 가져오기
        memberService.updateLastLoginTime(memberId); // 최종 로그인 시간 업데이트

    }
}
