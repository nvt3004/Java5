package com.fpoly.thainv.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.thainv.entities.Addresses;
import com.fpoly.thainv.entities.CustomerRoles;
import com.fpoly.thainv.entities.Roles;
import com.fpoly.thainv.entities.Users;
import com.fpoly.thainv.filters.UserSpecification;
import com.fpoly.thainv.jpa.AddressJpa;
import com.fpoly.thainv.jpa.CustomerRoleJPA;
import com.fpoly.thainv.jpa.OrderJpa;
import com.fpoly.thainv.jpa.RoleJpa;
import com.fpoly.thainv.jpa.UserJpa;
import com.fpoly.thainv.models.Address;
import com.fpoly.thainv.models.AddressNhut;
import com.fpoly.thainv.models.OrderProductImageDTO;
import com.fpoly.thainv.models.User;
import com.fpoly.thainv.models.UserNhut;
import com.fpoly.thainv.services.EmailDeleteCustomerNhutService;

import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

/**
 * File: CustomerController.java Author: nnhut379 Created on: May 25, 2024
 */
@Controller
public class CustomerController {
	@Autowired
	UserJpa userJPA;
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
	EmailDeleteCustomerNhutService emailDeleteCustomerService;
	private Integer currentUserId = null;

	@GetMapping("/custommer-management")
	public String customer(Model model, @RequestParam(defaultValue = "0") int page, HttpServletRequest request) {
		// Lấy HttpSession từ request
		HttpSession session = request.getSession();
		// Đặt lại giá trị page và các bộ lọc trong session
		session.setAttribute("page", null);
		session.setAttribute("customerName", null);
		session.setAttribute("email", null);
		session.setAttribute("phone", null);
		session.setAttribute("roleId", null);
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
		Specification<Users> spec = UserSpecification.filterUsers(null, null, null, null, null);
		// Tìm kiếm người dùng và thêm vào model
		Page<Users> usersPage = userJPA.findAll(spec, pageable);
		model.addAttribute("users", usersPage);
		model.addAttribute("currentPage", page + 1); // Điều chỉnh lại giá trị page để hiển thị đúng
		model.addAttribute("totalPages", usersPage.getTotalPages());
		model.addAttribute("size", formSize);

		return "Admin/html/custommer-management";
	}

	@PostMapping("/custommer-management")
	public String customerPost(@RequestParam(required = false) String customerName,
			@RequestParam(required = false) String email, @RequestParam(required = false) String phone,
			@RequestParam(required = false, defaultValue = "-1") Integer roleId,
			@RequestParam(required = false, defaultValue = "false") Boolean isDeleted,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			HttpServletRequest request, Model model) {
		// Lấy HttpSession từ request
		HttpSession session = request.getSession();
		String formName = request.getParameter("formName");

		// Kiểm tra formName và lưu các giá trị vào session
		if (formName != null) {
			switch (formName) {
			case "formFilter":
				session.setAttribute("customerName", customerName);
				session.setAttribute("email", email);
				session.setAttribute("phone", phone);
				session.setAttribute("roleId", roleId);
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
		String ctmName = (String) session.getAttribute("customerName");
		String ctmEmail = (String) session.getAttribute("email");
		String ctmPhone = (String) session.getAttribute("phone");
		Integer ctmRoleId = (Integer) session.getAttribute("roleId") != null ? (Integer) session.getAttribute("roleId")
				: -1;
		Boolean ctmIsDeleted = (Boolean) session.getAttribute("isDeleted");
		Integer formSize = (Integer) session.getAttribute("size") != null ? (Integer) session.getAttribute("size") : 10;
		Integer formPage = (Integer) session.getAttribute("page") != null ? (Integer) session.getAttribute("page") : 0;

		// Debug output
		System.out.println(formSize);
		System.out.println(formPage);

		// Điều chỉnh giá trị page để tránh âm
		if (formPage > 0) {
			formPage -= 1;
		}

		// Xử lý các giá trị null hoặc không hợp lệ
		customerName = ctmName != null ? ctmName.trim() : null;
		email = ctmEmail != null ? ctmEmail.trim() : null;
		phone = ctmPhone != null ? ctmPhone.trim() : null;
		roleId = ctmRoleId != -1 ? ctmRoleId : null;
		isDeleted = ctmIsDeleted == null || !ctmIsDeleted ? null : true;

		// Tạo Pageable và Specification
		Pageable pageable = PageRequest.of(formPage, formSize);
		Specification<Users> spec = UserSpecification.filterUsers(customerName, email, phone, roleId, isDeleted);

		// Tìm kiếm người dùng và thêm vào model
		Page<Users> usersPage = userJPA.findAll(spec, pageable);
		model.addAttribute("users", usersPage);
		model.addAttribute("customerName", customerName);
		model.addAttribute("email", email);
		model.addAttribute("phone", phone);
		model.addAttribute("roleId", roleId);
		model.addAttribute("isDeleted", isDeleted);
		model.addAttribute("currentPage", formPage + 1);
		model.addAttribute("totalPages", usersPage.getTotalPages());
		model.addAttribute("size", formSize);

		return "Admin/html/custommer-management";
	}

	@PostMapping("/restore-customer")
	public String restoreCustomerPost(@RequestParam("id") int id, Model model) {
		Optional<Users> optional = userJPA.findById(String.valueOf(id));
		if (optional.isPresent()) {
			Users user = optional.get();
			user.setIsDeleted(false);
			userJPA.save(user);
		}
		return "redirect:/custommer-management";
	}

	@PostMapping("/delete-customer")
	public String deleteCustomerPost(@RequestParam("id") int id, Model model) throws MessagingException, IOException {
		Optional<Users> optional = userJPA.findById(String.valueOf(id));
		if (optional.isPresent()) {
			Users user = optional.get();
			emailDeleteCustomerService.sendEmail(user);
			user.setIsDeleted(true);
			userJPA.save(user);
		}
		return "redirect:/custommer-management";
	}

	@PostMapping("/delete-multi")
	public String deleteMultiPost(@RequestParam("selectedIds") List<Integer> id, Model model)
			throws MessagingException, IOException {
		List<Users> listUser = new ArrayList<Users>();
		for (Integer integer : id) {
			Optional<Users> optional = userJPA.findById(String.valueOf(integer));
			if (optional.isPresent()) {
				Users user = optional.get();
				if (!user.getIsDeleted()) {
					user.setIsDeleted(true);
					userJPA.save(user);
					listUser.add(user);
				}
			}
		}
		emailDeleteCustomerService.sendEmail(listUser);
		return "redirect:/custommer-management";
	}

	@PostMapping("/restore-multi")
	public String restoreMultiPost(@RequestParam("selectedIds") List<Integer> id, Model model) {
		for (Integer integer : id) {
			Optional<Users> optional = userJPA.findById(String.valueOf(integer));
			if (optional.isPresent()) {
				Users user = optional.get();
				user.setIsDeleted(false);
				userJPA.save(user);
			}
		}
		return "redirect:/custommer-management";
	}

	// form
	// form
	// form

	
	@GetMapping("/customer-management_form")
	public String customerManagementForm(@RequestParam(name = "id", defaultValue = "") Integer id, Model model) {
		currentUserId = id;
		Optional<Users> optional = java.util.Optional.empty();
		if (id != null) {
			optional = userJPA.findById(String.valueOf(id));
		}
		if (optional.isPresent()) {
			Users user = optional.get();
			UserNhut userModel = new UserNhut();
			userModel.setUserId(user.getUserId());
			userModel.setFirstName(user.getFirstName());
			userModel.setLastName(user.getLastName());
			userModel.setEmail(user.getEmail());
			userModel.setPhone(user.getPhone());
			userModel.setIsDeleted(user.getIsDeleted());

			AddressNhut addressModel = new AddressNhut();
			Addresses userAddress = user.getAddress();
			addressModel.setAddressId(userAddress.getAddressId());
			addressModel.setAddressLine1(userAddress.getAddressLine1());
			addressModel.setAddressLine2(userAddress.getAddressLine2());
			addressModel.setCity(userAddress.getCity());
			addressModel.setState(userAddress.getState());
			addressModel.setCountry(userAddress.getCountry());
			addressModel.setPostalCode(userAddress.getPostalCode());

			userModel.setAddress(addressModel);

			// Gán vai trò cho userModel tùy theo logic của ứng dụng của bạn
			userModel.setRole(user.getRoles().stream().findFirst() // Find the first role (if any)
					.map(Roles::getRoleId) // Map the Roles object to its roleId
					.orElse(-1)); // If no role found, use -1 as default roleId

			model.addAttribute("user", userModel);
		}
		if (currentUserId != null) {
			List<OrderProductImageDTO> productImageDTOs = orderJPA.findOrderProductImagesByUserId(currentUserId);
			model.addAttribute("orderByUser", productImageDTOs);
		}
		if (currentUserId != null) {
			Integer prodSize = orderJPA.findOrderProductImagesByUserId(currentUserId).size();
			model.addAttribute("orderByUserSize", prodSize);
		}
		if (currentUserId != null) {
			Integer totalOrder = orderJPA.countTotalOrdersByUserId(currentUserId);
			model.addAttribute("countTotalOrders", totalOrder);
		}
		if (currentUserId != null) {
			Double AmountSpent = orderJPA.sumTotalAmountSpentByUserId(currentUserId);
			model.addAttribute("sumTotalAmountSpent", AmountSpent);
		}
		if (currentUserId != null) {
			Integer totalProd = orderJPA.sumTotalProductsBoughtByUserId(currentUserId);
			model.addAttribute("sumTotalProductsBought", totalProd);
		}
		return "Admin/html/customer-management_form";
	}

	@PostMapping("/customer-management_form")
	public String customerManagementFormPost(Model model, @Valid UserNhut user, BindingResult userErrors,
			@Valid AddressNhut address, BindingResult addressErrors) {
		currentUserId = user.getUserId();
		Users userEntity = new Users();
		Optional<Users> optional = userJPA.findById(String.valueOf(user.getUserId()));
		if (optional.isPresent()) {
			userEntity = optional.get();
			user.setPassword(optional.get().getPassword());
			userEntity.setPassword(optional.get().getPassword());
		} else {
			return "Admin/html/customer-management_form";
		}

		userEntity.setUserId(user.getUserId());
		userEntity.setFirstName(user.getFirstName());
		userEntity.setLastName(user.getLastName());
		userEntity.setEmail(user.getEmail());
		userEntity.setPhone(user.getPhone());
		userEntity.setIsDeleted(user.getIsDeleted());

		Addresses addressEntity = new Addresses();

		addressEntity.setAddressId(address.getAddressId());
		addressEntity.setAddressLine1(address.getAddressLine1());
		addressEntity.setAddressLine2(address.getAddressLine2());
		addressEntity.setCity(address.getCity());
		addressEntity.setCountry(address.getCountry());
		addressEntity.setState(address.getState());
		addressEntity.setPostalCode(address.getPostalCode());

		userEntity.setAddress(addressEntity);
		if (userErrors.hasErrors() || addressErrors.hasErrors()) {
			// Lấy ra danh sách lỗi của user
			List<FieldError> userFieldErrors = userErrors.getFieldErrors();
			for (FieldError error : userFieldErrors) {
				System.out.println("Lỗi user: " + error.getField() + " - " + error.getDefaultMessage());
			}

			// Lấy ra danh sách lỗi của address
			List<FieldError> addressFieldErrors = addressErrors.getFieldErrors();
			for (FieldError error : addressFieldErrors) {
				System.out.println("Lỗi address: " + error.getField() + " - " + error.getDefaultMessage());
			}

			// Thêm lỗi vào model để hiển thị trên giao diện
			model.addAttribute("userErrors", userErrors);
			model.addAttribute("addressErrors", addressErrors);

			// Thêm user và address vào model để hiển thị lại trên form
			user.setAddress(address);
			model.addAttribute("user", user);

			// Trả về view để hiển thị lỗi
			return "Admin/html/customer-management_form";
		} else {

			// Kiểm tra xem vai trò mới và cũ có sẵn không
			Roles oldRole = userEntity.getRoles().stream().findFirst()
					.orElseThrow(() -> new RuntimeException("Current role not found"));
			Roles newRole = roleJPA.findById(user.getRole()).orElseThrow(() -> new RuntimeException("Role not found"));

			// Xóa vai trò cũ
			CustomerRoles oldCustomerRole = new CustomerRoles(userEntity, oldRole);
			customerRoleJPA.delete(oldCustomerRole);

			// Thêm vai trò mới
			CustomerRoles newCustomerRole = new CustomerRoles(userEntity, newRole);
			customerRoleJPA.save(newCustomerRole);

			// Lưu người dùng và địa chỉ vào cơ sở dữ liệu
			addressJPA.save(addressEntity);
			userJPA.save(userEntity);

			user.setAddress(address);
			model.addAttribute("user", user);
			System.out.println("Cập nhật thanh công !");

			if (currentUserId != null) {
				List<OrderProductImageDTO> productImageDTOs = orderJPA.findOrderProductImagesByUserId(currentUserId);
				model.addAttribute("orderByUser", productImageDTOs);
			}
			if (currentUserId != null) {
				Integer prodSize = orderJPA.findOrderProductImagesByUserId(currentUserId).size();
				model.addAttribute("orderByUserSize", prodSize);
			}
			if (currentUserId != null) {
				Integer totalOrder = orderJPA.countTotalOrdersByUserId(currentUserId);
				model.addAttribute("countTotalOrders", totalOrder);
			}
			if (currentUserId != null) {
				Double AmountSpent = orderJPA.sumTotalAmountSpentByUserId(currentUserId);
				model.addAttribute("sumTotalAmountSpent", AmountSpent);
			}
			if (currentUserId != null) {
				Integer totalProd = orderJPA.sumTotalProductsBoughtByUserId(currentUserId);
				model.addAttribute("sumTotalProductsBought", totalProd);
			}

			return "Admin/html/customer-management_form";
		}
	}

	@ModelAttribute("roles")
	public List<Roles> getRoles() {
		return roleJPA.findAll();
	}

}
