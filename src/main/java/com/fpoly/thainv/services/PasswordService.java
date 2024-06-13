package com.fpoly.thainv.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PasswordService {

    private final PasswordServiceHelper helper;

    @Autowired
    public PasswordService(PasswordServiceHelper helper) {
        this.helper = helper;
    }

    public String generateRandomPassword(int length) {
        return helper.generateRandomPassword(length);
    }

    public String encodePassword(String rawPassword) {
        return rawPassword;
    }
}