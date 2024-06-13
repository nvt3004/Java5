package com.fpoly.thainv.controllers;

import java.awt.PageAttributes.MediaType;
import java.io.IOException;
import java.net.http.HttpHeaders;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fpoly.thainv.entities.AttributeProduct;
import com.fpoly.thainv.entities.Attributes;
import com.fpoly.thainv.entities.Categories;
import com.fpoly.thainv.entities.Images;
import com.fpoly.thainv.entities.Manufacturers;
import com.fpoly.thainv.entities.Products;
import com.fpoly.thainv.jpa.AttributeJPA;
import com.fpoly.thainv.jpa.AttributeProductJPA;
import com.fpoly.thainv.jpa.CategoryJPA;
import com.fpoly.thainv.jpa.ImageJPA;
import com.fpoly.thainv.jpa.ManufacturerJPA;
import com.fpoly.thainv.jpa.ProductJPA;
import com.fpoly.thainv.models.AttributeProductModel;
import com.fpoly.thainv.models.Product;
import com.fpoly.thainv.models.ProductVersion;
import com.fpoly.thainv.services.ExcelSevice;
import com.fpoly.thainv.services.UploadServices;

import jakarta.validation.Valid;

@Controller
public class ProductController {

	@Autowired
	ProductJPA productJPA;

	@Autowired
	CategoryJPA categoryJPA;

	@Autowired
	ImageJPA imageJPA;

	@Autowired
	ManufacturerJPA manufacturerJPA;

	@Autowired
	AttributeJPA attributeJPA;

	@Autowired
	UploadServices upload;

	@Autowired
	AttributeProductJPA attributeProductJPA;

	@GetMapping("/management/product")
	public String getProduct(Model model, @RequestParam(name = "productName", defaultValue = "") String productName,
			@RequestParam(name = "idCategory", defaultValue = "-1") int idCategory,
			@RequestParam("page") Optional<Integer> pageNum) {

		Pageable page = PageRequest.of(pageNum.orElse(1) - 1, 5);
		Page<Products> pages = productJPA.selectAllByStatus(true, "%" + productName + "%", page);

		if (idCategory != -1) {
			pages = productJPA.selectAllByStatus(true, "%" + productName + "%", idCategory, page);
		}

		List<Product> products = new ArrayList<>();

		if (model.containsAttribute("errUpdateVersion")) {
			String errorUpdateVersion = (String) model.getAttribute("errUpdateVersion");
			String errVersion = (String) model.getAttribute("errVersion");

			model.addAttribute("errUpdateVersion", errorUpdateVersion);
			model.addAttribute("errVersion", errVersion);
		}

		for (Products pd : pages) {
			Product productModel = new Product();

			productModel.setProductId(pd.getProductId());
			productModel.setImage(pd.getImageses().iterator().next().getImgUrl());
			productModel.setProductName(pd.getProductName());
			productModel.setManufactuters(pd.getManufacturers());
			productModel.setCategory(pd.getCategories());
			productModel.setRetailPrice(pd.getRetailPrice());
			productModel.setWholesalePrice(pd.getWholesalePrice());
			productModel.setImportPrice(pd.getImportPrice());
			productModel.setQuantity(pd.getStockQuantity());

			List<AttributeProductModel> versions = getVersionProduct(String.valueOf(pd.getProductId()));
			productModel.setVersions(versions);

			products.add(productModel);
		}

		model.addAttribute("products", products);
		model.addAttribute("productPage", pages);
		model.addAttribute("totalPages", pages.getTotalPages());
		model.addAttribute("namePd", productName);
		model.addAttribute("categoryId", idCategory);
		model.addAttribute("isPage", true);

		return "Admin/html/management-product";
	}

	@GetMapping("/management/product/{pageNumber}")
	public String getProductPage(Model model, @PathVariable("pageNumber") Optional<Integer> pageNumber,
			@RequestParam(name = "productName", defaultValue = "") String productName,
			@RequestParam(name = "idCategory", defaultValue = "-1") int idCategory) {

		return String.format("redirect:/management/product?productName=%s&idCategory=%s&page=%s", productName,
				idCategory, pageNumber.orElse(1));
	}

	@GetMapping("/management/product/add")
	public String productDetail(Model model) {

		if (model.containsAttribute("errManufacturer")) {
			String errorManufacturer = (String) model.getAttribute("errManufacturer");
			model.addAttribute("errManufacturer", errorManufacturer);
		}

		if (model.containsAttribute("errCatogory")) {
			String errCatogory = (String) model.getAttribute("errCatogory");
			model.addAttribute("errCatogory", errCatogory);
		}

		if (model.containsAttribute("errAttribute")) {
			String errAttribute = (String) model.getAttribute("errAttribute");
			boolean isContanSize = errAttribute.toLowerCase().contains("size");

			model.addAttribute(String.format("errAttribute%s", isContanSize ? "Size" : "Color"), errAttribute);
		}

		return "Admin/html/management-product-detail";
	}

	@GetMapping("/management/product/edit/{idProduct}")
	public String productEdit(Model model, @PathVariable("idProduct") String idProduct) {
		Optional<Products> productOptional = productJPA.findById(idProduct);

		if (model.containsAttribute("errManufacturer")) {
			String errorManufacturer = (String) model.getAttribute("errManufacturer");
			model.addAttribute("errManufacturer", errorManufacturer);
		}

		if (model.containsAttribute("errCatogory")) {
			String errCatogory = (String) model.getAttribute("errCatogory");
			model.addAttribute("errCatogory", errCatogory);
		}

		if (model.containsAttribute("errAttribute")) {
			String errAttribute = (String) model.getAttribute("errAttribute");
			boolean isContanSize = errAttribute.toLowerCase().contains("size");

			model.addAttribute(String.format("errAttribute%s", isContanSize ? "Size" : "Color"), errAttribute);
		}

		if (productOptional.isPresent()) {
			Products pdEntity = productOptional.get();
			Product productModel = new Product();

			productModel.setProductName(pdEntity.getProductName());
			productModel.setCategoryId(pdEntity.getCategories().getCategoryId());
			productModel.setManufacturerId(pdEntity.getManufacturers().getManufacturerId());
			productModel.setRetailPrice(pdEntity.getRetailPrice());
			productModel.setWholesalePrice(pdEntity.getWholesalePrice());
			productModel.setImportPrice(pdEntity.getImportPrice());
			productModel.setDescription(pdEntity.getDescription());
			productModel.setProductId(pdEntity.getProductId());

			List<AttributeProductModel> versions = getVersionProduct(idProduct);

			model.addAttribute("versions", versions);
			model.addAttribute("images", new ArrayList<>(pdEntity.getImageses()));
			model.addAttribute("pd", productModel);
		}

		model.addAttribute("isUpdate", true);
		return "Admin/html/management-product-detail";
	}

	@PostMapping("/management/product/edit/{idProduct}")
	public String productDetailEdit(@RequestParam("productTableData") String productTableData, @Valid Product product,
			BindingResult errors, @PathVariable("idProduct") String idProduct, Model model, RedirectAttributes params) {
		Optional<Products> pdOptional = productJPA.findById(idProduct);
		List<AttributeProductModel> versions = getVersionProduct(idProduct);
		Categories categoryEntity = new Categories();
		Manufacturers manufactureEntity = new Manufacturers();

		int countError = 0;

		if (!errors.hasErrors() && pdOptional.isPresent() && countError == 0) {
			Products pdEntity = pdOptional.get();
			List<ProductVersion> productVersions = getProductVersionJson(productTableData);

			List<ProductVersion> oldProducts = findProductVersionByOld(productVersions, true);
			List<ProductVersion> newProducts = findProductVersionByOld(productVersions, false);

			// Cập nhật sản phẩm gốc
			categoryEntity.setCategoryId(product.getCategoryId());
			manufactureEntity.setManufacturerId(product.getManufacturerId());

			pdEntity.setCategories(categoryEntity);
			pdEntity.setManufacturers(manufactureEntity);

			pdEntity.setProductName(product.getProductName());
			pdEntity.setRetailPrice(product.getRetailPrice());
			pdEntity.setWholesalePrice(product.getWholesalePrice());
			pdEntity.setImportPrice(product.getImportPrice());
			pdEntity.setDescription(product.getDescription());

			Products productSaved = productJPA.save(pdEntity);
			saveImages(product.getImageUrls(), productSaved);

			// Xóa phiên bản sản phẩm
			for (int i = 0; i < versions.size(); i++) {
				AttributeProductModel version = versions.get(i);
				boolean check = false;

				for (ProductVersion old : oldProducts) {
					if (old.getIdSize() == version.getSizeId() && old.getIdColor() == version.getColorId()) {
						check = true;
						break;
					}
				}

				if (!check) {
					attributeProductJPA.deleteById(String.valueOf(version.getAttributeIdSize()));
					attributeProductJPA.deleteById(String.valueOf(version.getAttributeIdColor()));
				}
			}

			// Thêm phiên bản sản phẩm

			boolean isDuplicateVersion = checkForDuplicateVersion(idProduct, newProducts);

			if (isDuplicateVersion) {
				model.addAttribute("errDulicateVersion", "Duplicate versions cannot be added");
				model.addAttribute("isUpdate", true);
				model.addAttribute("pd", product);
				model.addAttribute("versions", versions);
				model.addAttribute("images", new ArrayList<>(pdEntity.getImageses()));
				return "Admin/html/management-product-detail";
			} else {
				saveProductVersion(newProducts, productSaved);
				params.addFlashAttribute("successUpdateProduct", "Cập nhật sản phẩm thành công!");
			}
		} else {
			model.addAttribute("errors", errors);
			model.addAttribute("isUpdate", true);
			model.addAttribute("pd", product);
			model.addAttribute("versions", versions);
			model.addAttribute("images", new ArrayList<>(pdOptional.get().getImageses()));
			return "Admin/html/management-product-detail";
		}

		return String.format("redirect:/management/product/edit/%s", idProduct);
	}

	@PostMapping("/management/product/add")
	public String productDetailAdd(@RequestParam("productTableData") String productTableData, @Valid Product product,
			BindingResult errors, Model model) {
		Products productEntity = new Products();
		Categories categoryEntity = new Categories();
		Manufacturers manufactureEntity = new Manufacturers();

		int countError = 0;

		if (product.getSizeId()[0].equals("-1")) {
			model.addAttribute("errSize", "Plase select size");
			countError++;
		}

		if (product.getColorId()[0].equals("-1")) {
			model.addAttribute("errColor", "Plase select color");
			countError++;
		}

		if (product.getImageUrls().get(0).getOriginalFilename().length() == 0) {
			model.addAttribute("errImage", "Please select an image");
			countError++;
		}

		if (!errors.hasErrors() && countError == 0) {
			List<ProductVersion> productVersions = getProductVersionJson(productTableData);

			categoryEntity.setCategoryId(product.getCategoryId());
			manufactureEntity.setManufacturerId(product.getManufacturerId());

			productEntity.setCategories(categoryEntity);
			productEntity.setManufacturers(manufactureEntity);

			productEntity.setProductName(product.getProductName());
			productEntity.setRetailPrice(product.getRetailPrice());
			productEntity.setWholesalePrice(product.getWholesalePrice());
			productEntity.setImportPrice(product.getImportPrice());
			productEntity.setDescription(product.getDescription());
			productEntity.setStockQuantity(0);
			productEntity.setIsDeleted(true);

			Products productSaved = productJPA.save(productEntity);
			saveImages(product.getImageUrls(), productSaved);
			saveProductVersion(productVersions, productSaved);
			model.addAttribute("successAddProduct", "Thêm sản phẩm thành công");
		} else {
			model.addAttribute("errors", errors);
			model.addAttribute("pd", product);
		}

		return "Admin/html/management-product-detail";
	}

	@PostMapping("/management/product/delete")
	public String deleteProduct(@RequestParam("productId") String productId, Model model, RedirectAttributes params) {
		Optional<Products> productOptional = productJPA.findById(productId);

		if (productOptional.isPresent()) {
			Products productEntity = productOptional.get();
			productEntity.setIsDeleted(false);

			productJPA.save(productEntity);
			params.addFlashAttribute("deleteProduct", "Xóa thành công!");
		}

		return "redirect:/management/product";
	}

	@PostMapping("/management/product/deletes")
	public String deleteProducts(@RequestParam("pdremoves") String idRemoves, Model model, RedirectAttributes params) {
		String[] listProductId = idRemoves.split(", ");

		for (String id : listProductId) {
			Optional<Products> pdOptional = productJPA.findById(id);

			if (pdOptional.isPresent()) {
				Products productEntity = pdOptional.get();
				productEntity.setIsDeleted(false);

				productJPA.save(productEntity);
			}
		}

		params.addFlashAttribute("deleteProduct", "Xóa thành công!");
		return "redirect:/management/product";
	}

	@GetMapping("/management/product/restore")
	public String restoreProduct(Model model, @RequestParam(name = "productName", defaultValue = "") String productName,
			@RequestParam(name = "idCategory", defaultValue = "-1") int idCategory,
			@RequestParam("page") Optional<Integer> pageNum) {

		Pageable page = PageRequest.of(pageNum.orElse(1) - 1, 5);
		Page<Products> pages = productJPA.selectAllByStatus(false, "%" + productName + "%", page);

		if (idCategory != -1) {
			pages = productJPA.selectAllByStatus(false, "%" + productName + "%", idCategory, page);
		}

		List<Product> products = new ArrayList<>();

		if (model.containsAttribute("errUpdateVersion")) {
			String errorUpdateVersion = (String) model.getAttribute("errUpdateVersion");
			String errVersion = (String) model.getAttribute("errVersion");

			model.addAttribute("errUpdateVersion", errorUpdateVersion);
			model.addAttribute("errVersion", errVersion);
		}

		for (Products pd : pages) {
			Product productModel = new Product();

			productModel.setProductId(pd.getProductId());
			productModel.setImage(pd.getImageses().iterator().next().getImgUrl());
			productModel.setProductName(pd.getProductName());
			productModel.setManufactuters(pd.getManufacturers());
			productModel.setCategory(pd.getCategories());
			productModel.setRetailPrice(pd.getRetailPrice());
			productModel.setWholesalePrice(pd.getWholesalePrice());
			productModel.setImportPrice(pd.getImportPrice());
			productModel.setQuantity(pd.getStockQuantity());

			List<AttributeProductModel> versions = getVersionProduct(String.valueOf(pd.getProductId()));
			productModel.setVersions(versions);

			products.add(productModel);
		}

		model.addAttribute("products", products);
		model.addAttribute("productPage", pages);
		model.addAttribute("totalPages", pages.getTotalPages());
		model.addAttribute("namePd", productName);
		model.addAttribute("categoryId", idCategory);
		model.addAttribute("isPage", true);

		return "Admin/html/management-product-restore";
	}

	@GetMapping("/management/product/restore/{pageNumber}")
	public String getProductRestorePage(Model model, @PathVariable("pageNumber") Optional<Integer> pageNumber,
			@RequestParam(name = "productName", defaultValue = "") String productName,
			@RequestParam(name = "idCategory", defaultValue = "-1") int idCategory) {

		return String.format("redirect:/management/product/restore?productName=%s&idCategory=%s&page=%s", productName,
				idCategory, pageNumber.orElse(1));
	}

	/// management/product/restore/deletes

	@PostMapping("/management/product/restore/delete")
	public String restoreProduct(@RequestParam("productId") String productId, Model model, RedirectAttributes params) {
		Optional<Products> productOptional = productJPA.findById(productId);

		if (productOptional.isPresent()) {
			Products productEntity = productOptional.get();
			productEntity.setIsDeleted(true);

			productJPA.save(productEntity);
			params.addFlashAttribute("deleteProduct", "Xóa thành công!");
		}

		return "redirect:/management/product/restore";
	}

	@PostMapping("/management/product/restore/deletes")
	public String restoreProducts(@RequestParam("pdremoves") String idRemoves, Model model, RedirectAttributes params) {
		String[] listProductId = idRemoves.split(", ");

		for (String id : listProductId) {
			Optional<Products> pdOptional = productJPA.findById(id);

			if (pdOptional.isPresent()) {
				Products productEntity = pdOptional.get();
				productEntity.setIsDeleted(true);

				productJPA.save(productEntity);
			}
		}

		params.addFlashAttribute("deleteProduct", "Xóa thành công!");
		return "redirect:/management/product/restore";
	}

	@Autowired
	ExcelSevice excelExportService;

	@GetMapping("/export/products")
	public ResponseEntity<ByteArrayResource> exportProductsToExcel() throws IOException {

		byte[] data = excelExportService.exportProductsToExcel(productJPA.findAll());

		ByteArrayResource resource = new ByteArrayResource(data);

		return ResponseEntity.ok()
				.header(org.springframework.http.HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=products.xlsx")
				.contentType(org.springframework.http.MediaType.APPLICATION_OCTET_STREAM).contentLength(data.length)
				.body(resource);
	}

	public void saveProductVersion(List<ProductVersion> productVersions, Products pdEntity) {
		if (productVersions.size() == 0) {
			return;
		}

		for (ProductVersion version : productVersions) {
			Attributes color = new Attributes();
			Attributes size = new Attributes();
			AttributeProduct attributeProductSize = new AttributeProduct();
			AttributeProduct attributeProductColor = new AttributeProduct();

			color.setAttributeId(version.getIdColor());
			size.setAttributeId(version.getIdSize());

			attributeProductSize.setAttributes(size);
			attributeProductSize.setProducts(pdEntity);
			attributeProductSize.setQuantity(0);

			attributeProductColor.setAttributes(color);
			attributeProductColor.setProducts(pdEntity);
			attributeProductColor.setQuantity(null);

			attributeProductJPA.save(attributeProductSize);
			attributeProductJPA.save(attributeProductColor);
		}
	}

	@ModelAttribute("categories")
	public List<Categories> getCategories() {
		return categoryJPA.findAll();
	}

	@ModelAttribute("manufacturers")
	public List<Manufacturers> geManufacturers() {
		return manufacturerJPA.findAll();
	}

	@ModelAttribute("attributes")
	public List<Attributes> getAttribute() {
		List<Attributes> list = attributeJPA.findAll();
		return list;
	}

	public List<ProductVersion> getProductVersionJson(String productTableData) {
		ObjectMapper objectMapper = new ObjectMapper();
		try {
			List<ProductVersion> products = objectMapper.readValue(productTableData,
					new com.fasterxml.jackson.core.type.TypeReference<List<ProductVersion>>() {
					});

			return products;
		} catch (IOException e) {
			e.printStackTrace();
		}

		return new ArrayList<ProductVersion>();
	}

	public void saveImages(List<MultipartFile> files, Products pdEntity) {
		for (MultipartFile file : files) {
			if (file.getOriginalFilename().trim().length() == 0) {
				continue;
			}

			String image = upload.save(file, "images");

			if (image != null) {
				Images imageEntity = new Images();

				imageEntity.setImgUrl(image);
				imageEntity.setProducts(pdEntity);
				imageEntity.setFeedbacks(null);

				imageJPA.save(imageEntity);
			}
		}
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

	public List<ProductVersion> findProductVersionByOld(List<ProductVersion> list, boolean old) {
		List<ProductVersion> newList = new ArrayList<>();

		for (ProductVersion vs : list) {
			if (vs.isOld() == old) {
				newList.add(vs);
			}
		}

		return newList;
	}

	public boolean checkForDuplicateVersion(String idProduct, List<ProductVersion> products) {
		for (ProductVersion versionAdd : products) {
			for (AttributeProductModel versionSql : getVersionProduct(idProduct)) {
				if (versionAdd.getIdColor() == versionSql.getColorId()
						&& versionAdd.getIdSize() == versionSql.getSizeId()) {
					return true;
				}
			}
		}

		return false;
	}

}
