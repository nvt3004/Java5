package com.fpoly.thainv.models;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.hibernate.validator.constraints.Length;
import org.springframework.web.multipart.MultipartFile;

import com.fpoly.thainv.entities.Categories;
import com.fpoly.thainv.entities.Manufacturers;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Null;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
	private int productId;
	
	@NotBlank(message = "Please enter the product name")
	@NotEmpty(message = "Please enter the product name")
	@Length(min = 1, max = 255, message = "Product name must be between 1 - 255 characters")
	private String productName;
	
	@Min(value = 0, message = "Please select a category")
	private int categoryId;
	
	@Min(value = 0, message = "Please select a manufacturer")
	private int manufacturerId;
	
	private String sizeId[];
	
	private String colorId[];
	
	
	@NotNull(message = "Please enter the retail price")
	@DecimalMin(value = "1", message = "The retail price should not be negative")
	private BigDecimal retailPrice;
	
	@NotNull(message = "Please enter the wholesale price")
	@DecimalMin(value = "1", message = "The wholesale price should not be negative")
	private BigDecimal wholesalePrice;
	
	@NotNull(message = "Please enter the import price")
	@DecimalMin(value = "1", message = "The import price price should not be negative")
	private BigDecimal importPrice;
	
	
	private String description;
	private int stockQuantity;
	private List<MultipartFile> imageUrls;
	private String image;
	private Manufacturers manufactuters;
	private Categories category;
	private List<AttributeProductModel> versions = new ArrayList<>();
	private int quantity;
}
