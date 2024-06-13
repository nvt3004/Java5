package com.fpoly.thainv.controllers;

import java.util.Date;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fpoly.thainv.entities.LoginHistory;
import com.fpoly.thainv.entities.Roles;
import com.fpoly.thainv.entities.Users;
import com.fpoly.thainv.jpa.AuthJpa;
import com.fpoly.thainv.jpa.LoginHistoryJpa;
import com.fpoly.thainv.jpa.RoleJpa;
import com.fpoly.thainv.models.User;
import com.fpoly.thainv.services.EmailService;
import com.fpoly.thainv.services.OtpService;
import com.fpoly.thainv.services.PasswordService;
import com.fpoly.thainv.services.PasswordServiceHelper;
import com.fpoly.thainv.services.TemporaryUserService;
import com.fpoly.thainv.services.UserService;
import com.fpoly.thainv.untils.CookieUtil;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@Controller
public class AuthController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private RoleJpa roleJpa;

	@Autowired
	private AuthJpa authJpa;

	@Autowired
	private OtpService otpService;

	@Autowired
	private TemporaryUserService temporaryUserService;

	@Autowired
	private LoginHistoryJpa loginHistoryJpa;

	@Autowired
	private PasswordService passwordService;

	@Autowired
	private EmailService emailService;

	@Autowired
	private UserService userService;

	@Autowired
	private PasswordServiceHelper passwordServiceHelper;

	private static final String LOGIN_VIEW = "Admin/html/auth-login-basic";
	private static final String REDIRECT_LOGIN = "redirect:/login";
	// Home client
	private static final String HOME_VIEW = "Admin/Client/index";
	private static final String REDIRECT_HOME = "redirect:/home";

	// Home admin
	private static final String HOME_ADMIN_VIEW = "Admin/html/app-ecommerce-dashboard";
	private static final String REDIRECT_HOME_ADMIN = "redirect:/admin";

	private static final String REGISTER_VIEW = "Admin/html/auth-register-basic";
	private static final String REDIRECT_REGISTER = "redirect:/register";

	private static final String FORGOT_PASSWORD_VIEW = "Admin/html/auth-forgot-password-basic";
	private static final String REDIRECTFORGOT_PASSWORD = "redirect:/forgot-password";

	@GetMapping("/forgot-password")
	public String forgotPassword() {
		return FORGOT_PASSWORD_VIEW;
	}

	// @GetMapping("/home")
	// public String clientHome() {
	// 	return HOME_VIEW;
	// }

	@GetMapping("/admin")
	public String adminHome() {
		return HOME_ADMIN_VIEW;
	}

	@GetMapping("/login")
	public String getDoLogin(@RequestParam(name = "path", defaultValue = "login") String path, Model model,
			RedirectAttributes redirectAttributes) {
		System.out.println("Vào get login" + path);
		String email = CookieUtil.get(request, "email");
		String password = CookieUtil.get(request, "password");

		User user = new User();
		if (email != null && !email.isEmpty()) {
			user.setUserId(email);
			user.setPassword(password);
		}

		if (model.containsAttribute("user")) {
			user = (User) model.getAttribute("user");
		}
		redirectAttributes.addFlashAttribute("path", path);
		redirectAttributes.addFlashAttribute("user", user);
		return LOGIN_VIEW;
	}

	@PostMapping("/login")
	public String login(@Valid User user, BindingResult errors, Model model, RedirectAttributes redirectAttributes,
			HttpServletResponse response) {
		if (errors.hasFieldErrors("email") || errors.hasFieldErrors("password")) {
			redirectAttributes.addFlashAttribute("errors", errors);
			redirectAttributes.addFlashAttribute("user", user);
			return "redirect:/login";
		}

		Optional<Users> userOptional = authJpa.findByEmail(user.getEmail());

		if (userOptional.isEmpty()) {
			Users userEntity = userOptional.get();
			redirectAttributes.addFlashAttribute("user", userEntity);
			redirectAttributes.addFlashAttribute("error", "Invalid account information!");
			return "redirect:/login";
		}

		Users userEntity = userOptional.get();
		saveLoginHistory(userEntity);
		Set<Roles> roles = userEntity.getRoles();

		String redirectUrl = roles.stream().anyMatch(role -> role.getRoleName().equals("Admin")) ? "/admin" : "/home";
		CookieUtil.add(response, "email", userEntity.getEmail(), 5);
		CookieUtil.add(response, "password", userEntity.getPassword(), 5);
		return "redirect:" + redirectUrl;
	}

	private void saveLoginHistory(Users user) {
		LoginHistory loginHistory = new LoginHistory();
		loginHistory.setUsers(user);
		loginHistory.setLoginTime(new Date());
		loginHistoryJpa.save(loginHistory);
	}

	@GetMapping("/register")
	public String register() {
		return REGISTER_VIEW;
	}

	@PostMapping("/register")
	public String register(@Valid User user, BindingResult errors, RedirectAttributes redirectAttributes) {
		if (errors.getFieldError("email") != null || errors.getFieldError("password") != null
				|| errors.getFieldError("firstName") != null || errors.getFieldError("lastName") != null
				|| errors.getFieldError("phone") != null) {
			redirectAttributes.addFlashAttribute("errors", errors);
			redirectAttributes.addFlashAttribute("user", user);
			return REDIRECT_REGISTER;
		}

		Optional<Users> existingUser = authJpa.findByEmail(user.getEmail());
		if (existingUser.isPresent()) {
			redirectAttributes.addFlashAttribute("errorRegister", "Email already exists");
			redirectAttributes.addFlashAttribute("user", user);
			return REDIRECT_REGISTER;
		}
		Optional<Users> existingUserByPhone = authJpa.findByPhone(user.getPhone());
		if (existingUserByPhone.isPresent()) {
			redirectAttributes.addFlashAttribute("errorRegister", "Phone number already exists");
			redirectAttributes.addFlashAttribute("user", user);
			return REDIRECT_REGISTER;
		}

		try {
			otpService.generateAndSendOtp(user.getEmail());
			temporaryUserService.saveTemporaryUser(user);
			redirectAttributes.addFlashAttribute("otpSent", true);
			redirectAttributes.addFlashAttribute("user", user);
			return REDIRECT_REGISTER;
		} catch (Exception e) {
			System.out.println("Đã có lỗi" + e);
			redirectAttributes.addFlashAttribute("errors", "Error sending OTP. Please try again.");
			return REDIRECT_REGISTER;
		}
	}

	@PostMapping("/confirm-otp-register")
	public String confirmOtpRegister(@ModelAttribute("user") User user, @RequestParam("num1") String num1,
			@RequestParam("num2") String num2, @RequestParam("num3") String num3, @RequestParam("num4") String num4,
			@RequestParam("num5") String num5, @RequestParam("num6") String num6, @RequestParam("email") String email,
			@RequestParam("phone") String phone, RedirectAttributes redirectAttributes) {
		System.out.println(email + " Email");
		String otp = num1 + num2 + num3 + num4 + num5 + num6;
		System.out.println("Confirm register");
		try {
			if (otpService.validateOtp(email, otp)) {
				User tempUser = temporaryUserService.getTemporaryUser(phone);
				Users userEntity = new Users();
				userEntity.setEmail(tempUser.getEmail());
				userEntity.setPassword(tempUser.getPassword());
				userEntity.setFirstName(tempUser.getFirstName());
				userEntity.setLastName(tempUser.getLastName());
				userEntity.setPhone(tempUser.getPhone());

				// Set role mặc định cho người dùng
				Optional<Roles> defaultRole = roleJpa.findByRoleName("User");
				if (defaultRole.isPresent()) {
					userEntity.getRoles().add(defaultRole.get());
				}

				authJpa.save(userEntity);

				otpService.deleteOtp(email);
				temporaryUserService.deleteTemporaryUser(phone);
				redirectAttributes.addFlashAttribute("message", "Registration successful! Please log in.");
				return REDIRECT_LOGIN;
			} else {
				redirectAttributes.addFlashAttribute("user", user);
				redirectAttributes.addFlashAttribute("confirmOtpError", "Invalid OTP. Please try again.");
				return REDIRECT_REGISTER;
			}
		} catch (Exception e) {
			redirectAttributes.addFlashAttribute("user", user);
			redirectAttributes.addFlashAttribute("confirmOtpError", "An error occurred. Please try again.");
		}
		return "redirect:/otp";
	}

	@PostMapping("/forgot-password")
	public String forgotPassword(@Valid User user, BindingResult errors, Model model,
			RedirectAttributes redirectAttributes) {
		if (errors.getFieldError("email") != null || errors.getFieldError("phone") != null) {
			redirectAttributes.addFlashAttribute("errors", errors);
			redirectAttributes.addFlashAttribute("user", user);
			return REDIRECTFORGOT_PASSWORD;
		} else {
			Optional<Users> userOptional = authJpa.findByEmail(user.getEmail());
			System.out.println(userOptional.get().getEmail() + " Emai người dùng nhập vào ");
			if (userOptional.isEmpty()) {
				redirectAttributes.addFlashAttribute("user", user);
				redirectAttributes.addFlashAttribute("error", "Invalid account information!");
				return REDIRECTFORGOT_PASSWORD;
			} else {
				Users userEntity = userOptional.get();
				if (!userEntity.getPhone().equals(user.getPhone())) {
					redirectAttributes.addFlashAttribute("user", user);
					redirectAttributes.addFlashAttribute("error", "Invalid account information!");
					return REDIRECTFORGOT_PASSWORD;
				} else {
					otpService.generateAndSendOtp(userEntity.getEmail());
					temporaryUserService.saveTemporaryUser(user);
					redirectAttributes.addFlashAttribute("otpSent", true);
					redirectAttributes.addFlashAttribute("user", user);
					return REDIRECTFORGOT_PASSWORD;
				}

			}
		}
	}

	@PostMapping("/confirm-otp-forgot")
	public String confirmOtpForgot(@RequestParam("num1") String num1, @RequestParam("num2") String num2,
			@RequestParam("num3") String num3, @RequestParam("num4") String num4, @RequestParam("num5") String num5,
			@RequestParam("num6") String num6, @RequestParam("email") String email, @RequestParam("phone") String phone,
			RedirectAttributes redirectAttributes) {
		String otp = num1 + num2 + num3 + num4 + num5 + num6;
		System.out.println("vào xác thực forgot");

		try {
			if (otpService.validateOtp(email, otp)) {
				String newPassword = passwordService.generateRandomPassword(10);
				emailService.sendNewPasswordEmail(email, newPassword);
				userService.updatePassword(email, newPassword);
				otpService.deleteOtp(email);
				temporaryUserService.deleteTemporaryUser(phone);
				redirectAttributes.addFlashAttribute("message",
						"A new password has been sent to your email. Please check your inbox and use the new password to log in.");
				return "redirect:/login";
			} else {
				Optional<Users> userOptinal = authJpa.findByEmail(email);
				redirectAttributes.addFlashAttribute("user", userOptinal.get());
				redirectAttributes.addFlashAttribute("confirmOtpError", "Invalid OTP. Please try again.");
				return "redirect:/forgot-password";
			}
		} catch (Exception e) {
			System.out.println("An error occurred: " + e);
			Optional<Users> userOptinal = authJpa.findByEmail(email);
			redirectAttributes.addFlashAttribute("user", userOptinal.get());
			redirectAttributes.addFlashAttribute("confirmOtpError", "An error occurred. Please try again.");
			return "redirect:/forgot-password";
		}
	}

}
