package com.example.demo.frontController;


import com.example.demo.dto.UserDto;
import com.example.demo.service.CookieService;
import com.example.demo.service.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
@RequestMapping("/user")
public class UserController {

    private final UserService userService;
    private final CookieService cookieService;

    @GetMapping("/add")
    public String addForm(Model model) {

        model.addAttribute("user", new UserDto());

        return "/user/addForm";
    }

    @PostMapping("/add")
    public String saveUser(@ModelAttribute("user") UserDto user, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes) {

        // 유효성 체크
        userService.checkValidForSaveUser(user, bindingResult);

        // 에러 있으면 다시 input 폼으로
        if (bindingResult.hasErrors()) {
            return "/user/addForm";
        }
        
        // 문제 없으면 회원가입 진행
        UserDto result = userService.saveUser(user);
        
        redirectAttributes.addAttribute("userId", result.getUserId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/user/{userId}";
    }

    @GetMapping("/{userId}")
    public String userDetail(HttpServletRequest request,
                             @PathVariable String userId, Model model) {

        boolean isLogin = cookieService.isHasCookies(request, "loginUserId", userId);

        if (!isLogin) {
            return  "redirect:/user/login";
        }

        UserDto user = userService.findUserByUserID(userId);

        model.addAttribute("isLogin", true);
        model.addAttribute("user", user);

        return "/user/userDetail";
    }

    @GetMapping("/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new UserDto());

        return "/user/login";
    }

    @PostMapping("/login")
    public String login(HttpServletResponse response,
                        @ModelAttribute("user") UserDto userDto, BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {

        // 유효성 체크
        userService.checkValidForLogin(userDto, bindingResult);

        // 에러 있으면 다시 로그인 폼으로
        if (bindingResult.hasErrors()) {
            // 굳이 모델에 담을 필요 X
            return "/user/login";
        }

        // 문제 없으면 user 반환
        UserDto user = userService.findUserByUserID(userDto.getUserId());

        // 로그인 성공하면 로그인정보 쿠키에 저장
        Integer maxAge = 2*60*60;  // 2시간
        Cookie cookie =  cookieService.makeCookie("loginUserId", user.getUserId(), "/", maxAge);
        response.addCookie(cookie);

        redirectAttributes.addAttribute("userId", user.getUserId());
        return "redirect:/user/{userId}";
    }

    @GetMapping("/all")
    public String allUserList(HttpServletRequest request, Model model) {

        // 로그인 안된 상태이면 로그인 페이지로 리다이렉트
        boolean isLogin = cookieService.isHasCookies(request, "loginUserId");

        // 쿠키가 없으면 로그인페이지로 리다이렉트
        if (!isLogin) {
            return "redirect:/user/login";
        }

        List<UserDto> userList = userService.findAllUser();

        model.addAttribute("isLogin", true);
        model.addAttribute("userList", userList);

        return "/user/userList";
    }

    @GetMapping("/logout")
    public String logout(HttpServletResponse response) {

        // loginUserId 쿠키 제거
        Cookie cookie = cookieService.makeCookie("loginUserId", "", "/", 0);
        response.addCookie(cookie);
        
        return "redirect:/";
    }
}
