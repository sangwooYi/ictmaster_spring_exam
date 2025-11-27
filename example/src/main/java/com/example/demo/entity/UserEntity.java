package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name="user_master")
public class UserEntity {

    @Id
    @Column(name="user_id")
    private String userId;

    @Column(name="user_name", nullable = false)
    private String userName;

    @Column(nullable = false)
    private String password;

    @Column(name="phone_number",  nullable = false)
    private String phoneNumber;
}
