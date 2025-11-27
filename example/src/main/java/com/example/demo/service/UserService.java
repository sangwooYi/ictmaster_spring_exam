package com.example.demo.service;

import com.example.demo.dto.UserDto;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

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

}
