package com.fpoly.thainv.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Address {
	@NotNull(message = "Address ID cannot be null")
    @NotBlank(message = "Please enter address line 1")
    @Size(max = 100, message = "Address should not exceed 100 characters")
    private String addressId;
    @NotBlank(message = "Please enter address line 1")
    @Size(max = 100, message = "Address line 1 should not exceed 100 characters")
    private String addressLine1;

    private String addressLine2;

    @NotBlank(message = "Address Line One cannot be blank")
    @Size(max = 255, message = "Address Line One must be less than or equal to 255 characters")
    private String addressLineOne;
    
    @NotBlank(message = "Address Line One cannot be blank")
    @Size(max = 255, message = "Address Line Two must be less than or equal to 255 characters")
    private String addressLineTwo;

    @NotBlank(message = "City cannot be blank")
    @Size(max = 100, message = "City must be less than or equal to 100 characters")
    private String city;

    @NotBlank(message = "State cannot be blank")
    @Size(max = 100, message = "State must be less than or equal to 100 characters")
    private String state;

    @NotBlank(message = "Country cannot be blank")
    @Size(max = 100, message = "Country must be less than or equal to 100 characters")
    private String country;

    @NotBlank(message = "Postal Code cannot be blank")
    @Pattern(regexp = "\\d{5}", message = "Postal Code must be exactly 5 digits")
    private String postalCode;


}
