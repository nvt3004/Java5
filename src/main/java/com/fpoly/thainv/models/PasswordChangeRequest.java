package com.fpoly.thainv.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PasswordChangeRequest {
	@NotBlank(message = "Please enter password")
	@Size(min = 6, message = "Passwords must be at least 6 characters")
	private String newPassword;
	@NotBlank(message = "Please enter password")
	@Size(min = 6, message = "Passwords must be at least 6 characters")
    private String confirmPassword;
}
