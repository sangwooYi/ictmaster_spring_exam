package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    /**
     * 유저 회원가입
     * @param user
     * @return
     * 이미 존재하는 경우는 null 반환
     * 회원가입 성공하면 가입 후 userDto 만들어 반환
     */
    public UserDto saveUser(UserDto user) {

        UserDto curUser = this.findUserByUserID(user.getUserId());

        // 이미 존재하는 경우에는 null 반환 (이거로 이미 존재여부 판단)
        if (!ObjectUtils.isEmpty(curUser)) {
            return null;
        }

        UserEntity userEntity = this.convertToEntity(user);
        UserEntity result = userRepository.save(userEntity);

        UserDto resultDto = this.convertToUserDto(result);

        return resultDto;
    }

    public UserDto findUserByUserID(String userId) {

        UserEntity userEntity = userRepository.findUserByUserId(userId).orElse(null);

        UserDto userDto = this.convertToUserDto(userEntity);

        return userDto;
    }

    /**
     * 
     * @param userId
     * @param password
     * @return ID로 존재하는 회원이 없던지, 비밀번호가 틀리던지 null 반환
     * 성공하면 userDto 반환
     */
    public UserDto login(String userId, String password) {

        UserDto userDto = this.findUserByUserID(userId);

        // 아이디로 존재하는 회원 X
        if (ObjectUtils.isEmpty(userDto)) {
            return null;
        }

        // 비밀번호 불일치
        if (!password.equals(userDto.getPassword())) {
            return null;
        }

        return userDto;

    }

    /**
     * 전체 회원리스트 userDto 리스트 형태로 반환
     * @return
     */
    public List<UserDto> findAllUser() {

        List<UserEntity> userEntityList = userRepository.findAll();

        List<UserDto> userDtoList = new ArrayList<>();

        userEntityList.forEach((userEntity) -> {

            UserDto userDto = this.convertToUserDto(userEntity);
            userDtoList.add(userDto);
        });

        return userDtoList;
    }

    /**
     * DTO -> Entity 변환
     * @param user
     * @return
     */
    public UserEntity convertToEntity(UserDto user) {

        if (ObjectUtils.isEmpty(user)){
            return null;
        }

        UserEntity userEntity = new UserEntity();

        userEntity.setUserName(user.getUserName());
        userEntity.setUserId(user.getUserId());
        userEntity.setPassword(user.getPassword());
        userEntity.setPhoneNumber(user.getPhoneNumber());

        return userEntity;
    }

    /**
     * Entity -> DTO 변환
     * @param user
     * @return
     */
    public UserDto convertToUserDto(UserEntity user) {

        if (ObjectUtils.isEmpty(user)){
            return null;
        }

        UserDto userDto = new UserDto();

        userDto.setUserName(user.getUserName());
        userDto.setUserId(user.getUserId());
        userDto.setPassword(user.getPassword());
        userDto.setPhoneNumber(user.getPhoneNumber());

        return userDto;

    }

    /**
     *  회원가입 관련 유효성 체크
     */
    public void checkValidForSaveUser(UserDto user, BindingResult bindingResult) {
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

        UserDto findUser = this.findUserByUserID(user.getUserId());

        if (!ObjectUtils.isEmpty(findUser)) {
            bindingResult.addError(new FieldError("user", "userId", "이미 존재하는 ID 입니다."));
        }
    }

    /**
     *  로그인 유효성 체크
     */
    public void checkValidForLogin(UserDto userDto, BindingResult bindingResult) {

        if (!StringUtils.hasText(userDto.getUserId())) {
            bindingResult.addError(new FieldError("user", "userId", "아이디를 입력해 주세요"));
        }

        if (!StringUtils.hasText(userDto.getPassword())) {
            bindingResult.addError(new FieldError("user", "password", "비밀번호를 입력해 주세요"));
        }

        UserDto user = this.findUserByUserID(userDto.getUserId());

        if (ObjectUtils.isEmpty(user) || !user.getPassword().equals(userDto.getPassword())) {
            bindingResult.addError(new ObjectError("loginFail", "아이디 혹은 비밀번호가 일치하지 않습니다."));
        }
    }

    /**
     * 쿠키 존재여부 파악
     * @param request
     * @param cookieName
     * @return
     */
    public boolean isHasCookies(HttpServletRequest request, String cookieName) {

        boolean isLogin = false;

        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {

            if (cookie.getName().equals(cookieName)) {
                isLogin = true;
                break;
            }
        }
        return isLogin;
    }

    /**
     * 값 일치까지 확인
     * @param request
     * @param cookieName
     * @param cookieValue
     * @return
     */
    public boolean isHasCookies(HttpServletRequest request, String cookieName, String cookieValue) {
        boolean isLogin = false;
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie : cookies) {
            // 실제 유저 ID와 동일한지도 체크 , 아니면 로그인화면으로 리다이렉트
            if (cookie.getName().equals(cookieName) && cookie.getValue().equals(cookieValue)) {
                isLogin = true;
                break;
            }
        }
        return isLogin;
    }

}
