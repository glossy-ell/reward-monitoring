package com.example.reward_monitoring.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.web.session.SessionInformationExpiredEvent;
import org.springframework.security.web.session.SessionInformationExpiredStrategy;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSessionExpiredStrategy implements SessionInformationExpiredStrategy {

    @Override
    public void onExpiredSessionDetected(SessionInformationExpiredEvent event) throws IOException, ServletException {
        HttpServletRequest request = event.getRequest();
        HttpServletResponse response = event.getResponse();
        HttpSession session = request.getSession();

        SessionInformation sessionInformation = event.getSessionInformation();
        long currentTime = System.currentTimeMillis();
        long lastRequestTime = sessionInformation.getLastRequest().getTime();
        long sessionTimeoutInMillis = 30 * 60 * 1000L;
        if ((currentTime - lastRequestTime) >= sessionTimeoutInMillis) { //마지막 요청과 30분 이상 차이날경우 세션만료 간주
            // 세션 타임아웃으로 인한 만료
            session.setAttribute("SESSION_TIMEOUT", true);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"SESSION_TIMEOUT\",\"message\":\"세션이 만료되었습니다. 다시 로그인 해주세요.\"}");
        } else {
            // 중복 로그인으로 인한 세션 만료
            session.setAttribute("DUPLICATE_LOGIN", true);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\":\"DUPLICATE_LOGIN\",\"message\":\"중복 로그인 감지. 다시 로그인 해주세요.\"}");
        }

    }
}