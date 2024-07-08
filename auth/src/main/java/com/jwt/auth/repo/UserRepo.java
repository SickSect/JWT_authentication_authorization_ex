package com.jwt.auth.repo;

import com.jwt.auth.model.UserEntity;
import org.springframework.stereotype.Repository;

@Repository
public class UserRepo {
    public UserEntity findUserByEmail(String email){
        /**
         * THIS IS MOCK
         */
        UserEntity user = new UserEntity();
        user.setEmail(email);
        user.setPassword("{noop}password");
        user.setFirstName("firstName");
        user.setLastName("lastName");
        return user;
    }
}
