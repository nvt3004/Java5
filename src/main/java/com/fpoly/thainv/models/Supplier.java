package com.fpoly.thainv.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

/**
 * File: Supplier.java
 * Author: nnhut379
 * Created on: May 25, 2024
 */
@Getter
@Setter
public class Supplier extends AddressNhut {
	  
	    private Integer supplierId;

	    @NotBlank(message = "Supplier Name cannot be blank")
	    @Size(max = 255, message = "Supplier Name must be less than or equal to 255 characters")
	    private String supplierName;

	    @NotBlank(message = "Contact Name cannot be blank")
	    @Size(max = 255, message = "Contact Name must be less than or equal to 255 characters")
	    private String contactName;

	    @NotBlank(message = "Email cannot be blank")
	    @Size(max = 100, message = "Email must be at most 100 characters")
	    @Email(message = "Email should be valid")
	    private String email;

	    @NotBlank(message = "Phone cannot be blank")
	    @Pattern(
	        regexp = "0[0-9]{9}",
	        message = "Phone number must start with 0 and be 10 digits long"
	    )
	    private String phone;
		private Boolean isDeleted; 
//	    @NotNull(message = "Address ID cannot be null")
//	    private Integer addressId;
}

