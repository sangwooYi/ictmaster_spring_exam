package com.example.demo.dto;

import lombok.Data;

@Data
public class UserDto {

    private String userId;
    private String userName;
    private String password;
    private String passwordRe;
    private String phoneNumber;

}
