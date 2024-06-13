package com.fpoly.thainv.controllers;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.thainv.entities.Images;
import com.fpoly.thainv.jpa.ImageJPA;
import com.fpoly.thainv.services.UploadServices;

@Controller
public class ImageController {
	@Autowired
	ImageJPA imageJPA;

	@Autowired
	UploadServices upload;
	
	@GetMapping("/product/image/delete/{idImage}")
	public String deleteImageProduct(RedirectAttributes params, @PathVariable("idImage") String idImage) {
		Optional<Images> imageOptinal = imageJPA.findById(idImage);
		
		if(imageOptinal.isPresent()) {
			Images imageDelete = imageOptinal.get();
			
			upload.delete(imageDelete.getImgUrl(), "images");
			imageJPA.delete(imageDelete);
			params.addFlashAttribute("successDeleteImageProduct", "Xóa sản phẩm thành công!");
		}
		
		return String.format("redirect:/management/product/edit/%s",imageOptinal.get().getProducts().getProductId());
	}
}
