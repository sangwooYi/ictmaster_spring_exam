package com.example.demo.frontController;

import com.example.demo.dto.UserDto;
import com.example.demo.service.CookieService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@Controller
public class FrontController {

    private final CookieService cookieService;
    private final UserService userService;

    @GetMapping("/")
    public String goHome(HttpServletRequest request, Model model) {

        // 로그인 여부 체크
        boolean isLogin = cookieService.isHasCookies(request, "loginUserId");

        // 로그인 되어있으면 이름도 같이 반환
        if (isLogin) {

            String userId = cookieService.getCookieValue(request, "loginUserId");
            UserDto user = userService.findUserByUserID(userId);

            model.addAttribute("userName", user.getUserName());

        }
        

        model.addAttribute("isLogin", isLogin);

        return "index";
    }
}
