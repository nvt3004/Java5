package com.fpoly.thainv.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserNhut extends AddressNhut {
	
    private int userId;

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

	private String password;

	@NotBlank(message = "Please enter phone")
	@Pattern(regexp = "\\d{10}", message = "Phone number should be 10 digits")
	private String phone;

	private Boolean isDeleted;
	
	@Min(value = 0, message = "Please choose role" )
	private int role;
	
	private AddressNhut address;
}