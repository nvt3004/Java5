package com.fpoly.thainv.controllers;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.thainv.entities.Advertisements;
import com.fpoly.thainv.entities.Images;
import com.fpoly.thainv.entities.Products;
import com.fpoly.thainv.jpa.AdvJpa;
import com.fpoly.thainv.jpa.ImageJPA;
import com.fpoly.thainv.models.Product;
import com.fpoly.thainv.services.UploadServices;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class AdvController {
	@Autowired
	HttpSession session;

	@Autowired
	AdvJpa advJpa;

	@Autowired
	UploadServices upload;

	@Autowired
	ImageJPA imageJPA;

	@GetMapping("/adv")
	public String adv(Model model, @ModelAttribute("adv") Advertisements advertisements) {
		System.out.println("Đã đi vào adv");
		List<Advertisements> advList = advJpa.findAll();
		if (advList == null || advList.isEmpty()) {
			System.out.println("Đã null");
			return "Admin/html/adv";
		}
		System.out.println("Không null");

		model.addAttribute("advs", advList);
		System.out.println("id = " + advList.get(0).getAdName());
		if ((advertisements != null && advertisements.getImg() != null)) {
			String[] img = advertisements.getImg().split(",");
			model.addAttribute("images", img);
		}
		return "Admin/html/adv";
	}

	@GetMapping("/adv/update")
	public String updateAdv(Model model, @RequestParam("id") int id, RedirectAttributes redirectAttributes) {
		System.out.println("Sửa");
		Optional<Advertisements> advOptional = advJpa.findById(String.valueOf(id));
		// System.out.println("Dia chi
		// "+userOptional.get().getAddress().getAddressLine1());
		if (advOptional.isPresent()) {
			Advertisements advEntity = advOptional.get();
			redirectAttributes.addFlashAttribute("adv", advEntity);
			System.out.println("name = " + advEntity.getAdName());
		} else {
			System.out.println("Adv not found!");
		}
		redirectAttributes.addFlashAttribute("isUpdate", true);

		return "redirect:/adv";
	}

	@PostMapping("/adv/update")
	public String postUpdateAdv(Advertisements advertisements, Model model, @RequestParam("id") int id,
			RedirectAttributes redirectAttributes, @RequestParam("imageUrls") List<MultipartFile> files) {

		Optional<Advertisements> advOptional = advJpa.findById(String.valueOf(id));
		if (advOptional.isPresent()) {
			Advertisements advEntity = advOptional.get();
			advEntity.setAdName(advertisements.getAdName());
			advEntity.setAdDescription(advertisements.getAdDescription());
			advEntity.setStartDate(advertisements.getStartDate());
			advEntity.setEndDate(advertisements.getEndDate());

			String image = advEntity.getImg();

			for (MultipartFile multipartFile : files) {
				String img = upload.save(multipartFile, "images");
				image += "," + img;
			}

			advEntity.setImg(image);

			advJpa.save(advEntity);
		} else {
			System.out.println("Adv not found!");
		}

		return "redirect:/adv";
	}

	@GetMapping("/adv/delete")
	public String deleteAdv(Model model, @RequestParam("id") int id, RedirectAttributes redirectAttributes) {
		Optional<Advertisements> advOptional = advJpa.findById(String.valueOf(id));
		if (advOptional.isPresent()) {
			Advertisements advEntity = advOptional.get();
			advJpa.delete(advEntity);
		} else {
			System.out.println("Adv not found!");
		}

		return "redirect:/adv";
	}

	@GetMapping("adv/image/delete/{name}/{adId}")
	public String advDeleteImg(@PathVariable("name") String name, @PathVariable("adId") String adId,
			RedirectAttributes attributes) {
		System.out.println("Name = " + name);
		System.out.println("id = " + adId);
		Optional<Advertisements> advertisements = advJpa.findById(adId);
		if (advertisements.isPresent()) {
			Advertisements advertisements2 = advertisements.get();
			String[] imgs = advertisements2.getImg().split(",");
			String temp = "";
			for (String string : imgs) {
				if (!string.equalsIgnoreCase(name)) {
					temp += string + ",";
				}
			}
			if (temp.endsWith(",")) {
				temp = temp.substring(0, temp.length() - 1);
			}
			advertisements2.setImg(temp.trim());
			System.out.println("Chuoi = " + temp);
			advJpa.save(advertisements2);
			attributes.addFlashAttribute("adv", advertisements2);
		}
		return "redirect:/adv";
	}

	@PostMapping("/adv/add")
	public String advAdd(@Valid @ModelAttribute Advertisements adv, BindingResult result, Model model,
			@RequestParam("startDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
			@RequestParam("endDate") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
			@RequestParam("imageUrls") List<MultipartFile> files) {
		adv.setStartDate(startDate);
		adv.setEndDate(endDate);

		if (result.hasErrors()) {
			model.addAttribute("errors", result);
			return "redirect:/adv"; // Thay thế bằng tên của template mẫu của bạn
		}

		if(endDate.isBefore(startDate)){
			return "redirect:/adv";
		}

		List<Advertisements> conflictingAds = advJpa.findConflictingAdvertisements(adv.getStartDate(),
				adv.getEndDate());

		if (!conflictingAds.isEmpty()) {
			System.out.println("Đã tới đây");
			model.addAttribute("error", "Khoảng thời gian đã trùng với quảng cáo khác.");
			return "redirect:/adv"; // Thay thế bằng tên của template mẫu của bạn
		}

		System.out.println("Đã đi vào add");
		System.out.println("Start Date: " + startDate);
		System.out.println("End Date: " + endDate);

		String image = "";

		for (MultipartFile multipartFile : files) {
			String img = upload.save(multipartFile, "images");
			image += img + ",";
		}

		adv.setStartDate(startDate);
		adv.setEndDate(endDate);

		adv.setImg(image.substring(0, image.length() - 1).trim());

		// Lưu quảng cáo và trả về đối tượng đã lưu với ID được tạo tự động
		Advertisements savedAdv = advJpa.save(adv);

		return "redirect:/adv";
	}
	

}
