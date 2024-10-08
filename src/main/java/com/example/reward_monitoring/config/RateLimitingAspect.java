package com.example.reward_monitoring.config;


import com.example.reward_monitoring.general.member.entity.Member;
import com.example.reward_monitoring.general.member.repository.MemberRepository;
import io.github.bucket4j.Bucket;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.Objects;


@Aspect
@Component
@Slf4j
public class RateLimitingAspect {

    private final BucketConfiguration bucketConfiguration;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private HttpServletRequest request;

    @Autowired
    public RateLimitingAspect(BucketConfiguration bucketConfiguration) {
        this.bucketConfiguration = bucketConfiguration;
    }

    @Around("@within(org.springframework.stereotype.Controller) && @within(RateLimited)")
    public Object rateLimit(ProceedingJoinPoint joinPoint) throws Throwable {
        String userId = extractUserId(); // 사용자 ID 추출
        Bucket bucket = bucketConfiguration.getBucket(userId); // 사용자별 Bucket 가져오기

        if (!isRestController(joinPoint)) {
            return joinPoint.proceed();
        }
            if (bucket.tryConsume(1) && !userId.equals("none")) {
                log.info("userid: " + userId);
                log.info("bucket count consumed");
                return joinPoint.proceed(); // 토큰이 있는 경우, 메서드 실행
            } else {
                HttpServletRequest request = ((ServletRequestAttributes) Objects.requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
                HttpServletResponse response = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getResponse();
                if (userId.equals("none")) {
                    assert response != null;
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403 Forbidden
                    response.sendRedirect("/actLogout");
                    return null;
                }
                // REST API 요청에 대해 429 응답 반환
                response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
                return Map.of("status", HttpStatus.TOO_MANY_REQUESTS.value(), "안내", "과도한 요청을 보내셨습니다. 잠시후 다시 시도해주세요");

            }
    }



    private String extractUserId() {
        // 세션에서 사용자 ID 추출
        HttpSession session = request.getSession(false);
        if (session != null) {
            Member member = (Member) session.getAttribute("member"); // 세션에서 Member 객체 가져오기
            if (member != null) {
                return member.getId(); // 사용자 ID 반환
            }
        }
        return "none";
    }

    // @RestController 여부를 판단하는 함수
    private boolean isRestController(ProceedingJoinPoint joinPoint) {
        MethodSignature methodSignature = (MethodSignature) joinPoint.getSignature();
        Method method = methodSignature.getMethod();
        return method.isAnnotationPresent(ResponseBody.class);
    }

}
