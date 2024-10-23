package com.example.reward_monitoring.general;


import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ErrorController {

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request) {
        // 에러 코드에 따라 다른 페이지로 유도 가능
        Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");

        if (statusCode != null)
            if (statusCode == 404)
                return "error404";

        return "error";
    }

}
