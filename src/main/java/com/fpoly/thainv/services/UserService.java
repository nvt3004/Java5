package com.fpoly.thainv.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fpoly.thainv.entities.Users;
import com.fpoly.thainv.jpa.AuthJpa;
import com.fpoly.thainv.models.User;

@Service
public class UserService {

    private final AuthJpa authJpa;

    @Autowired
    public UserService(AuthJpa authJpa) {
        this.authJpa = authJpa;
    }

    public void updatePassword(String email, String newPassword) {
        Optional<Users> userOptinal = authJpa.findByEmail(email);
        if (userOptinal != null) {
        	Users user = userOptinal.get();
            user.setPassword(newPassword);
            authJpa.save(user);
        } else {
            throw new IllegalArgumentException("User not found");
        }
    }
}