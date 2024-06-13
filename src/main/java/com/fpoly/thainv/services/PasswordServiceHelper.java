package com.fpoly.thainv.services;

import java.security.SecureRandom;
import org.springframework.stereotype.Service;

@Service
public class PasswordServiceHelper {

    private static final String DIGITS = "0123456789";
    private static final SecureRandom random = new SecureRandom();
//    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String generateRandomPassword(int length) {
        StringBuilder password = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            password.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }
        return password.toString();
    }
//
//    public String encodePassword(String rawPassword) {
//        return passwordEncoder.encode(rawPassword);
//    }
//    
//
//	public boolean matches(String rawPassword, String encodedPassword) {
//		return passwordEncoder.matches(rawPassword, encodedPassword);
//	}
}