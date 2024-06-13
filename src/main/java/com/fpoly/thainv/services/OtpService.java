package com.fpoly.thainv.services;

import java.time.Duration;
import java.util.Random;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class OtpService {

	private static final int OTP_VALID_DURATION = 5;
	private static final int OTP_LENGTH = 6;

	private final RedisTemplate<String, String> redisTemplate;
	private final EmailService emailService;

	public OtpService(RedisTemplate<String, String> redisTemplate, EmailService emailService) {
		this.redisTemplate = redisTemplate;
		this.emailService = emailService;
	}

	public void generateAndSendOtp(String email) {
		String otp = generateOtp();
		redisTemplate.opsForValue().set(email, otp, Duration.ofMinutes(OTP_VALID_DURATION));
		emailService.sendOtpEmail(email, otp);
	}

	public boolean validateOtp(String email, String otp) {
		String storedOtp = redisTemplate.opsForValue().get(email);
		redisTemplate.delete(email);
		return otp.equals(storedOtp);
	}

	private String generateOtp() {
		Random random = new Random();
		StringBuilder otp = new StringBuilder();
		for (int i = 0; i < OTP_LENGTH; i++) {
			otp.append(random.nextInt(10));
		}
		return otp.toString();
	}
	
    public void deleteOtp(String email) {
        redisTemplate.delete(email);
    }
}