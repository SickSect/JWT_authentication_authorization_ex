package com.jwt.auth.model;

import lombok.Data;

@Data
public class UserEntity {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
}
