package com.fpoly.thainv.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.thainv.entities.Manufacturers;
import com.fpoly.thainv.jpa.ManufacturerJPA;
import com.fpoly.thainv.models.Product;

@Controller
public class ManufacturerController {

	@Autowired
	ManufacturerJPA manufacturerJPA;

	@PostMapping("/manufacturer/add/{path}")
	public String addManufacturer(@RequestParam("manufacturerName") Optional<String> manufacturerName,
			@RequestParam("country") Optional<String> country, @PathVariable("path") String path,
			RedirectAttributes params,Model model) {

		if (manufacturerName.isPresent()) {
			String name = manufacturerName.get();
			if (name.trim().length() > 0) {
				Manufacturers manufacturer = new Manufacturers();
				manufacturer.setManufacturerName(name);
				manufacturer.setCountry(country.get());

				manufacturerJPA.save(manufacturer);
				params.addFlashAttribute("addManufacturerSuccess", "Thêm thành công!");
			} else {
				if (path.equalsIgnoreCase("product")) {
					params.addFlashAttribute("errManufacturer", "The manufacturer's name must not be left blank");
				}else {
					model.addAttribute("errManufacturer", "The manufacturer's name must not be left blank");
				}
			}
		}
		return "redirect:/management/product/add";
	}
}
