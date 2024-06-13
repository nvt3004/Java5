package com.fpoly.thainv.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.thainv.entities.AttributeProduct;
import com.fpoly.thainv.entities.Attributes;
import com.fpoly.thainv.entities.Products;
import com.fpoly.thainv.jpa.AttributeProductJPA;
import com.fpoly.thainv.jpa.ProductJPA;
import com.fpoly.thainv.models.AttributeProductModel;

@Controller
public class AttributeProductController {
	@Autowired
	public AttributeProductJPA attributeProductJPA;

	@Autowired
	ProductJPA productJPA;

	@PostMapping("/attribute/product/update")
	public String updateAttributeProduct(RedirectAttributes params, @RequestParam("attributeSize") int attributeSize,
			@RequestParam("attributeColor") int attributeColor, @RequestParam("sizeId") int sizeId,
			@RequestParam("colorId") int colorId, @RequestParam("productId") String productId,
			@RequestParam("codeShow") String codeShow) {
		Optional<AttributeProduct> sizeOptional = attributeProductJPA.findById(String.valueOf(attributeSize));
		Optional<AttributeProduct> colorOptional = attributeProductJPA.findById(String.valueOf(attributeColor));

		if (sizeOptional.isPresent() && colorOptional.isPresent()) {
			AttributeProduct sizeProduct = sizeOptional.get();
			AttributeProduct colorProduct = colorOptional.get();

			Attributes attSize = new Attributes();
			Attributes attColor = new Attributes();

			attSize.setAttributeId(sizeId);
			attColor.setAttributeId(colorId);

			sizeProduct.setAttributes(attSize);
			colorProduct.setAttributes(attColor);

			boolean isDuplicateVersion = checkDuplicateVersion(productId, sizeId, colorId);
			if (isDuplicateVersion) {
			} else {
				attributeProductJPA.save(sizeProduct);
				attributeProductJPA.save(colorProduct);
				params.addFlashAttribute("updateVersion", "Cập nhật phiên bản thành công!");
			}
		}

		return "redirect:/management/product";
	}

	public boolean checkDuplicateVersion(String productId, int sizeId, int colorId) {
//		List<AttributeProductModel> versions = getVersionProduct(productId);
//		
//		for (AttributeProductModel version : versions) {
//			System.out.println(version.getName());
//		}

		return false;
	}

	public List<AttributeProductModel> getVersionProduct(String productId) {
		if (productId == null || productId.isEmpty()) {
			return new ArrayList<>();
		}

		Optional<Products> pdOptional = productJPA.findById(productId);
		List<AttributeProductModel> versions = new ArrayList<>();

		if (pdOptional.isPresent()) {
			Products pdEntity = pdOptional.get();
			Sort sort = Sort.by("attrPrdId");

			List<AttributeProduct> attributeProducts = attributeProductJPA
					.findAttributeByProductId(pdEntity.getProductId(), sort);

			for (int i = 2; i <= attributeProducts.size(); i += 2) {
				AttributeProduct attributeSize = attributeProducts.get(i - 2);
				AttributeProduct attributeColor = attributeProducts.get(i - 1);

				String name = String.format("%s - %s - %s", pdEntity.getProductName(),
						attributeColor.getAttributes().getValue(), attributeSize.getAttributes().getValue());

				int attributeIdSize = attributeSize.getAttrPrdId();
				int attributeIdColor = attributeColor.getAttrPrdId();
				int idSize = attributeSize.getAttributes().getAttributeId();
				int idColor = attributeColor.getAttributes().getAttributeId();
				int quantity = attributeSize.getQuantity();

				versions.add(
						new AttributeProductModel(attributeIdSize, attributeIdColor, name, idSize, idColor, quantity));
			}
		}

		return versions;
	}
}
