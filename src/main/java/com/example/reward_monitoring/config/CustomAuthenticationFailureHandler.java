package com.example.reward_monitoring.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import java.io.IOException;

public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        // 응답 상태를 401로 설정
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        // JSON 형식으로 에러 메시지 전송
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write("{\"error\": \"Invalid username or password\"}");
    }
}
