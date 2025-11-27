package com.example.demo.frontController;


import com.example.demo.dto.UserDto;
import com.example.demo.repository.UserRepository;
import com.example.demo.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Controller
public class FrontController {

    private final UserService userService;
    private final UserRepository userRepository;

    @GetMapping("/home")
    public String goHome() {
        return "redirect:/";
    }

    @GetMapping("/user/add")
    public String addForm(Model model) {

        model.addAttribute("user", new UserDto());

        return "/user/addForm";
    }

    @PostMapping("/user/add")
    public String saveUser(@ModelAttribute("user") UserDto user, BindingResult bindingResult,
                           RedirectAttributes redirectAttributes, Model model) {

        log.info("getObjectName = {}", bindingResult.getObjectName());
        log.info("getTarget = {}", bindingResult.getTarget());
        // 빈값 처리 체크
        if (!StringUtils.hasText(user.getUserId())) {
            bindingResult.addError(new FieldError("user", "userId", "유저 ID는 필수입니다."));
        }
        if (!StringUtils.hasText(user.getPassword())) {
            bindingResult.addError(new FieldError("user", "password", "비밀번호 입력은 필수입니다."));
        }
        if (!(user.getPassword().equals(user.getPasswordRe()))) {
            bindingResult.addError(new FieldError("user", "passwordRe", "비밀번호와 재입력비밀번호가 일치하지 않습니다."));
        }
        if (!StringUtils.hasText(user.getUserName())) {
            bindingResult.addError(new FieldError("user", "userName", "사용자 이름은 필수입니다. "));
        }


        // 연락처 넘어왔다면 정규식 검사 (숫자로만 10~11자리로 허용)
        String regex = "\\d{10,11}";
        if (!ObjectUtils.isEmpty(user.getPhoneNumber()) && !user.getPhoneNumber().matches(regex)) {
            bindingResult.addError(new FieldError("user", "phoneNumber", "핸드폰번호 형식이 적절하지 않습니다."));
        }

        UserDto findUser = userService.findUserByUserID(user.getUserId());

        if (!ObjectUtils.isEmpty(findUser)) {
            bindingResult.addError(new FieldError("user", "userId", "이미 존재하는 ID 입니다."));
        }

        // 에러 있으면 다시 input 폼으로
        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {}" , bindingResult);
            // 굳이 모델에 담을 필요 X
            return "/user/addForm";
        }
        
        // 문제 없으면 회원가입 진행
        UserDto result = userService.saveUser(user);
        
        redirectAttributes.addAttribute("userId", result.getUserId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/user/{userId}";
    }

    @GetMapping("/user/{userId}")
    public String userDetail(@PathVariable String userId, Model model) {

        UserDto user = userService.findUserByUserID(userId);

        model.addAttribute("user", user);

        return "/user/userDetail";
    }

    @GetMapping("/user/login")
    public String loginPage(Model model) {
        model.addAttribute("user", new UserDto());

        return "/user/login";
    }

    @PostMapping("/user/login")
    public String login(@ModelAttribute("user") UserDto userDto, BindingResult bindingResult,
                        RedirectAttributes redirectAttributes) {

        if (!StringUtils.hasText(userDto.getUserId())) {
            bindingResult.addError(new FieldError("user", "userId", "아이디를 입력해 주세요"));
        }

        if (!StringUtils.hasText(userDto.getPassword())) {
            bindingResult.addError(new FieldError("user", "password", "비밀번호를 입력해 주세요"));
        }

        UserDto user = userService.findUserByUserID(userDto.getUserId());

        if (ObjectUtils.isEmpty(user) || !user.getPassword().equals(userDto.getPassword())) {
            bindingResult.addError(new ObjectError("loginFail", "아이디 혹은 비밀번호가 일치하지 않습니다."));
        }

        // 에러 있으면 다시 로그인 폼으로
        if (bindingResult.hasErrors()) {
            log.info("bindingResult = {}" , bindingResult);
            // 굳이 모델에 담을 필요 X
            return "/user/login";
        }
        redirectAttributes.addAttribute("userId", user.getUserId());
        redirectAttributes.addAttribute("status", true);
        return "redirect:/user/{userId}";
    }

    @GetMapping("/user/all")
    public String allUserList(Model model) {

        List<UserDto> userList = userService.findAllUser();

        model.addAttribute("userList", userList);

        return "/user/userList";
    }

}
