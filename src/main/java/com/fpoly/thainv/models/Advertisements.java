package com.fpoly.thainv.models;

import java.time.LocalDate;
import java.util.Date;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Advertisements {
	private int adId;
	@NotNull(message = "Tên quảng cáo không được để trống")
    @NotBlank(message = "Tên quảng cáo không được để trống")
    private String adName;

    private String adDescription;
	@NotNull(message = "Img không được để trống")
    @NotBlank(message = "Img không được để trống")
    private String imageUrls;

    @NotNull(message = "Ngày bắt đầu không được để trống")
    @NotBlank(message = "Ngày bắt đầu không được để trống")
    @FutureOrPresent(message = "Ngày bắt đầu phải là tương lai hoặc hiện tại")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate;

    @NotNull(message = "Ngày kết thúc không được để trống")
    @NotBlank(message = "Ngày kết thúc không được để trống")
    @FutureOrPresent(message = "Ngày kết thúc phải là tương lai hoặc hiện tại")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;
}