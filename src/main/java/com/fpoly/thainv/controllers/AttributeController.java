package com.fpoly.thainv.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.thainv.entities.Attributes;
import com.fpoly.thainv.jpa.AttributeJPA;

@Controller
public class AttributeController {

	@Autowired
	public AttributeJPA attributeJPA;

	@PostMapping("/attribute/add/{path}/{property}")
	public String addAttribute(@RequestParam("name") Optional<String> attributeName, @PathVariable("path") String path,
			@PathVariable("property") String property, RedirectAttributes params, Model model) {

		if (attributeName.isPresent()) {
			String name = attributeName.get();
			boolean isSize = property.equalsIgnoreCase("size");

			if (name.trim().length() > 0) {
				Attributes attribute = new Attributes();

				attribute.setAttributeKey(isSize ? "Size" : "Color");
				attribute.setValue(name);

				Attributes attributeCheck = attributeJPA.findAttributeByKeyAndValue(attribute.getAttributeKey(), name);
				if (attributeCheck != null) {
					String error = String.format("%s name already exists, please choose another name",
							isSize ? "Size" : "Color");

					if (path.equalsIgnoreCase("product")) {
						params.addFlashAttribute("errAttribute", error);
					} else {
						model.addAttribute("errAttribute", error);
					}
				} else {
					attributeJPA.save(attribute);
				}
			} else {
				String error = String.format("The %s name must not be left blank", isSize ? "size" : "color");

				if (path.equalsIgnoreCase("product")) {
					params.addFlashAttribute("errAttribute", error);
				} else {
					model.addAttribute("errAttribute", error);
				}
			}
		}

		return "redirect:/management/product/add";
	}

}
