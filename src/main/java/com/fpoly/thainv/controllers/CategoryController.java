package com.fpoly.thainv.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.thainv.entities.Categories;
import com.fpoly.thainv.entities.Manufacturers;
import com.fpoly.thainv.jpa.CategoryJPA;
import com.fpoly.thainv.jpa.ManufacturerJPA;

@Controller
public class CategoryController {
	@Autowired
	CategoryJPA categoryJPA;

	@PostMapping("/category/add/{path}")
	public String addManufacturer(@RequestParam("name") Optional<String> categoryName,
			@RequestParam("description") Optional<String> description, @PathVariable("path") String path,
			RedirectAttributes params, Model model) {

		if (categoryName.isPresent()) {
			String name = categoryName.get();

			if (name.trim().length() > 0) {
				Categories category = new Categories();
				category.setCategoryName(name);
				category.setDescription(description.get());

				Categories catCheck = categoryJPA.findCategoryByName(name);
				if (catCheck != null) {
					if (path.equalsIgnoreCase("product")) {
						params.addFlashAttribute("errCategory",
								"Name of existing product type please select another name");
					} else {
						model.addAttribute("errCategory", "Name of existing product type please select another name");
					}
				} else {
					categoryJPA.save(category);
					params.addFlashAttribute("addCategorySuccess", "Thêm thành công!");
				}
			} else {
				if (path.equalsIgnoreCase("product")) {
					params.addFlashAttribute("errCategory", "The category name must not be left blank");
				} else {
					model.addAttribute("errCategory", "The category name must not be left blank");
				}
			}
		}
		return "redirect:/management/product/add";
	}
}
