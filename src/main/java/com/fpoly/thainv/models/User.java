package com.fpoly.thainv.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class User extends Address {
	@NotBlank(message = "Please enter username")
	@Size(max = 50, message = "Username should not exceed 50 characters")
	@Size(min = 6, message = "Username must be at least 6 characters")
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Username should not contain special characters")
    private String userId;

    @NotBlank(message = "Please enter first name")
    @Size(max = 50, message = "First name should not exceed 50 characters")
    @Size(min = 2, message = "First name must be at least 2 characters")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "First name should not contain special characters")
    private String firstName;

    @NotBlank(message = "Please enter last name")
    @Size(max = 50, message = "First name should not exceed 50 characters")
    @Size(min = 2, message = "Last name must be at least 2 characters")
    @Pattern(regexp = "^[\\p{L} .'-]+$", message = "Last name should not contain special characters")
    private String lastName;

	@NotBlank(message = "Please enter email")
	@Email(message = "Please enter a valid email")
	private String email;

	@NotBlank(message = "Please enter password")
	@Size(min = 6, message = "Passwords must be at least 6 characters")
	private String password;

	@NotBlank(message = "Please enter phone")
	@Pattern(regexp = "\\d{10}", message = "Phone number should be 10 digits")
	private String phone;
	
	@Min(value = 0, message = "Please choose role" )
	private int role;

}
