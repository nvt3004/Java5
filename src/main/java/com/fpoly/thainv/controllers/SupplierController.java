package com.fpoly.thainv.controllers;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.thainv.entities.Addresses;
import com.fpoly.thainv.entities.Suppliers;
import com.fpoly.thainv.filters.SupplierSpecification;
import com.fpoly.thainv.jpa.AddressJpa;
import com.fpoly.thainv.jpa.CustomerRoleJPA;
import com.fpoly.thainv.jpa.OrderJpa;
import com.fpoly.thainv.jpa.RoleJpa;
import com.fpoly.thainv.jpa.SupplierJPA;
import com.fpoly.thainv.jpa.UserJpa;
import com.fpoly.thainv.models.AddressNhut;
import com.fpoly.thainv.models.Supplier;
import com.fpoly.thainv.services.ExcelExportNhutService;
import com.fpoly.thainv.services.ExcelImportSupplierNhutService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
public class SupplierController {
	@Autowired
	UserJpa userJPA;
	@Autowired
	SupplierJPA supplierJPA;
	@Autowired
	RoleJpa roleJPA;
	@Autowired
	OrderJpa orderJPA;
	@Autowired
	CustomerRoleJPA customerRoleJPA;
	@Autowired
	AddressJpa addressJPA;
	@Autowired
	HttpSession session;
	@Autowired
	ExcelExportNhutService excelExportService;
	@Autowired
	ExcelImportSupplierNhutService excelImportSupplierService;

	@GetMapping("/supplier-management")
	public String supplier(Model model, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
		// Lấy HttpSession từ request
		HttpSession session = request.getSession();
		// Đặt lại giá trị page và các bộ lọc trong session
		session.setAttribute("page", 0);
		session.setAttribute("keyWordSupplier", null);
		session.setAttribute("isDeleted", null);
		// Lấy giá trị formSize từ session, nếu không có thì mặc định là 10
		Integer formSize = Optional.ofNullable((Integer) session.getAttribute("size")).orElse(10);
		// Điều chỉnh giá trị page để tránh âm
		if (page > 0) {
			page -= 1;
		} else {
			page = 0; // Đảm bảo rằng page không âm
		}
		// Tạo Pageable và Specification
		Pageable pageable = PageRequest.of(page, formSize);
		Specification<Suppliers> spec = SupplierSpecification.filterByKeyword(null, null);
		// Tìm kiếm người dùng và thêm vào model
		Page<Suppliers> suppliers = supplierJPA.findAll(spec, pageable);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("currentPage", page + 1); // Điều chỉnh lại giá trị page để hiển thị đúng
		model.addAttribute("totalPages", suppliers.getTotalPages());
		model.addAttribute("size", formSize);

		return "Admin/html/supplier-management";
	}

	@PostMapping("/supplier-management")
	public String supplierPost(Model model, @RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
			@RequestParam(required = false) String keyWordSupplier, @RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, HttpServletRequest request) {
		// Lấy HttpSession từ request
		HttpSession session = request.getSession();
		String formName = request.getParameter("formName");

		// Kiểm tra formName và lưu các giá trị vào session
		if (formName != null) {
			switch (formName) {
			case "formFilter":
				session.setAttribute("keyWordSupplier", keyWordSupplier);
				session.setAttribute("isDeleted", isDeleted);
				session.setAttribute("page", 0);
				break;
			case "formSize":
				session.setAttribute("size", size);
				session.setAttribute("page", 0);
				break;
			case "formPage":
				session.setAttribute("page", page);
				break;
			default:
				// Xử lý trường hợp formName không khớp
				break;
			}
		}
		// Lấy các giá trị từ session
		String ctmKeyWord = (String) session.getAttribute("keyWordSupplier");
		Boolean ctmIsDeleted = (Boolean) session.getAttribute("isDeleted");
		Integer formSize = (Integer) session.getAttribute("size") != null ? (Integer) session.getAttribute("size") : 10;
		Integer formPage = (Integer) session.getAttribute("page") != null ? (Integer) session.getAttribute("page") : 0;
		// Điều chỉnh giá trị page để tránh âm
		if (formPage > 0) {
			formPage -= 1;
		}
		// Xử lý các giá trị null hoặc không hợp lệ
		keyWordSupplier = ctmKeyWord != null ? ctmKeyWord.trim() : null;
		isDeleted = ctmIsDeleted == null || !ctmIsDeleted ? null : true;
		// Tạo Pageable và Specification
		Pageable pageable = PageRequest.of(formPage, formSize);
		Specification<Suppliers> spec = SupplierSpecification.filterByKeyword(keyWordSupplier, isDeleted);

		// Tìm kiếm người dùng và thêm vào model
		Page<Suppliers> suppliers = supplierJPA.findAll(spec, pageable);
		model.addAttribute("suppliers", suppliers);
		model.addAttribute("keyWordSupplier", keyWordSupplier);
		model.addAttribute("isDeleted", isDeleted);
		model.addAttribute("currentPage", formPage + 1);
		model.addAttribute("totalPages", suppliers.getTotalPages());
		model.addAttribute("size", formSize);
		return "Admin/html/supplier-management";
	}

	@PostMapping("/restore-supplier")
	public String restoreSupplierPost(@RequestParam("id") int supplierId, Model model) {
		Optional<Suppliers> optional = supplierJPA.findById(supplierId);
		if (optional.isPresent()) {
			Suppliers supplier = optional.get();
			supplier.setIsDeleted(false);
			supplierJPA.save(supplier);
		}
		return "redirect:/supplier-management";
	}

	@PostMapping("/delete-supplier")
	public String deleteSupplierPost(@RequestParam("id") int supplierId, Model model) {
		Optional<Suppliers> optional = supplierJPA.findById(supplierId);
		if (optional.isPresent()) {
			Suppliers supplier = optional.get();
			supplier.setIsDeleted(true);
			supplierJPA.save(supplier);
		}
		return "redirect:/supplier-management";
	}

	@PostMapping("/delete-supplier-multi")
	public String deleteMultiPost(@RequestParam("selectedIds") List<Integer> id, Model model) {
		for (Integer integer : id) {
			Optional<Suppliers> optional = supplierJPA.findById(integer);
			if (optional.isPresent()) {
				Suppliers suppliers = optional.get();
				suppliers.setIsDeleted(true);
				supplierJPA.save(suppliers);
			}
		}
		return "redirect:/supplier-management";
	}

	@PostMapping("/restore-supplier-multi")
	public String restoreMultiPost(@RequestParam("selectedIds") List<Integer> id, Model model) {
		for (Integer integer : id) {
			Optional<Suppliers> optional = supplierJPA.findById(integer);
			if (optional.isPresent()) {
				Suppliers suppliers = optional.get();
				suppliers.setIsDeleted(false);
				supplierJPA.save(suppliers);
			}
		}
		return "redirect:/supplier-management";
	}

	@PostMapping("/export-supplier-select")
	public ResponseEntity<InputStreamResource> exportSupplierSelect(@RequestParam("selectedIds") List<Integer> ids)
			throws IOException {
		List<Suppliers> suppliers = new ArrayList<>();
		for (Integer id : ids) {
			Optional<Suppliers> optionalSupplier = supplierJPA.findById(id);
			optionalSupplier.ifPresent(supplier -> {
				if (supplier.getIsDeleted() == null) {
					supplier.setIsDeleted(false);
				}
				suppliers.add(supplier);
			});
		}

		byte[] excelData = excelExportService.exportSuppliersToExcelAsByteArray(suppliers);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(excelData);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=suppliers.xlsx");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(new InputStreamResource(byteArrayInputStream));
	}

	@PostMapping("/export-supplier-all")
	public ResponseEntity<InputStreamResource> exportSupplierAll() throws IOException {
		List<Suppliers> suppliers = supplierJPA.findAll();
		for (Suppliers supplier : suppliers) {
			if (supplier.getIsDeleted() == null) {
				supplier.setIsDeleted(false);
			}
		}

		byte[] excelData = excelExportService.exportSuppliersToExcelAsByteArray(suppliers);
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(excelData);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=suppliers.xlsx");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(new InputStreamResource(byteArrayInputStream));
	}

	@GetMapping("/download-supplier-file-example")
	public ResponseEntity<InputStreamResource> downloadSupplierFileExample() throws IOException {
		byte[] excelData = excelExportService.generateSampleExcelTemplate();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(excelData);

		HttpHeaders headers = new HttpHeaders();
		headers.add("Content-Disposition", "attachment; filename=supplierExample.xlsx");

		return ResponseEntity.ok().headers(headers).contentType(MediaType.APPLICATION_OCTET_STREAM)
				.body(new InputStreamResource(byteArrayInputStream));
	}

	@PostMapping("/import-suppliers")
	public String importSuppliers(@RequestParam("file") MultipartFile file, RedirectAttributes redirectAttributes) {
		try {
			excelImportSupplierService.importSuppliersFromExcel(file);
			redirectAttributes.addFlashAttribute("message", "Import successful");
			redirectAttributes.addFlashAttribute("color", "alert-success");
		} catch (IOException e) {
			redirectAttributes.addFlashAttribute("message", "Import failed: " + e.getMessage());
			redirectAttributes.addFlashAttribute("color", "alert-danger");
		}
		return "redirect:/supplier-management";
	}

	// Form
	// Form
	// Form
	// Form
	// Form

	@GetMapping("/supplier-management-form")
	public String supplierManagementForm(Model model, @RequestParam(name = "id", required = false) Integer supplierId) {
		Optional<Suppliers> optional = Optional.empty();
		if (supplierId != null) {
			optional = supplierJPA.findById(supplierId);
		}
		if (optional.isPresent()) {
			Suppliers supplier = optional.get();
			model.addAttribute("supplier", supplier);
		} else {
			model.addAttribute("supplier", new Suppliers());
		}

		return "Admin/html/supplier-management-form";
	}

	@PostMapping("/supplier-form-add")
	public String supplierManagementFormPostAdd(Model model, @Valid Supplier supplier, BindingResult supplierError,
			@Valid AddressNhut address, BindingResult addressError) {
		Suppliers supplierEntity = new Suppliers();
		supplierEntity.setSupplierName(supplier.getSupplierName());
		supplierEntity.setContactName(supplier.getContactName());
		supplierEntity.setEmail(supplier.getEmail());
		supplierEntity.setPhone(supplier.getPhone());
		supplierEntity.setIsDeleted(supplier.getIsDeleted());

		Addresses addressEntity = new Addresses();
		addressEntity.setAddressLine1(address.getAddressLine1());
		addressEntity.setAddressLine2(address.getAddressLine2());
		addressEntity.setCity(address.getCity());
		addressEntity.setCountry(address.getCountry());
		addressEntity.setPostalCode(address.getPostalCode());
		addressEntity.setState(address.getState());

		supplierEntity.setAddresses(addressEntity);

		if (supplierError.hasErrors() || addressError.hasErrors()) {
			model.addAttribute("supplier", supplierEntity);
			model.addAttribute("supplierError", supplierError);
			model.addAttribute("addressError", addressError);
			StringBuilder errorMessage = new StringBuilder("Data entry error: ");

			if (supplierError.hasErrors()) {
				supplierError.getAllErrors()
						.forEach(error -> errorMessage.append(error.getDefaultMessage()).append(" "));
			}

			if (addressError.hasErrors()) {
				addressError.getAllErrors()
						.forEach(error -> errorMessage.append(error.getDefaultMessage()).append(" "));
			}

			model.addAttribute("message", errorMessage.toString());
			model.addAttribute("color", "alert-danger");
			return "Admin/html/supplier-management-form";
		} else {
			try {
				Addresses savedAddress = addressJPA.save(addressEntity);
				supplierEntity.setAddresses(savedAddress);
				supplierJPA.save(supplierEntity);
				model.addAttribute("message", "Congratulations! Supplier inserted successfully!");
				model.addAttribute("color", "alert-success");
				model.addAttribute("supplier", new Suppliers());
			} catch (Exception e) {
				model.addAttribute("supplier", supplierEntity);
				model.addAttribute("message", "Error: " + e.getMessage());
				model.addAttribute("color", "alert-danger");
			}
			return "Admin/html/supplier-management-form";
		}
	}

	@PostMapping("/supplier-form-update")
	public String supplierManagementFormPostUpdate(Model model,
			@RequestParam(name = "id", required = false) Integer supplierId,
			@RequestParam(name = "adrId", required = false) Integer addressId, @Valid Supplier supplier,
			BindingResult supplierError, @Valid AddressNhut address, BindingResult addressError) {

		Suppliers supplierEntity = new Suppliers();
		supplierEntity.setSupplierId(supplierId);
		supplierEntity.setSupplierName(supplier.getSupplierName());
		supplierEntity.setContactName(supplier.getContactName());
		supplierEntity.setEmail(supplier.getEmail());
		supplierEntity.setPhone(supplier.getPhone());
		supplierEntity.setIsDeleted(supplier.getIsDeleted());

		Addresses addressEntity = new Addresses();
		addressEntity.setAddressId(addressId);
		addressEntity.setAddressLine1(address.getAddressLine1());
		addressEntity.setAddressLine2(address.getAddressLine2());
		addressEntity.setCity(address.getCity());
		addressEntity.setCountry(address.getCountry());
		addressEntity.setPostalCode(address.getPostalCode());
		addressEntity.setState(address.getState());

		supplierEntity.setAddresses(addressEntity);

		if (supplierError.hasErrors() || addressError.hasErrors()) {
			model.addAttribute("supplier", supplierEntity);
			model.addAttribute("supplierError", supplierError);
			model.addAttribute("addressError", addressError);
			StringBuilder errorMessage = new StringBuilder("Data entry error: ");

			if (supplierError.hasErrors()) {
				supplierError.getAllErrors()
						.forEach(error -> errorMessage.append(error.getDefaultMessage()).append(" "));
			}

			if (addressError.hasErrors()) {
				addressError.getAllErrors()
						.forEach(error -> errorMessage.append(error.getDefaultMessage()).append(" "));
			}

			model.addAttribute("message", errorMessage.toString());
			model.addAttribute("color", "alert-danger");
			return "Admin/html/supplier-management-form";
		} else {
			try {
				Addresses savedAddress = addressJPA.save(addressEntity);
				supplierEntity.setAddresses(savedAddress);
				supplierJPA.save(supplierEntity);
				model.addAttribute("message", "Congratulations! Supplier updated successfully!");
				model.addAttribute("color", "alert-success");
				model.addAttribute("supplier", supplierEntity);
			} catch (Exception e) {
				model.addAttribute("supplier", supplierEntity);
				model.addAttribute("message", "Error: " + e.getMessage());
				model.addAttribute("color", "alert-danger");
			}
			return "Admin/html/supplier-management-form";

		}
	}
}
