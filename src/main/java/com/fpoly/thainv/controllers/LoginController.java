package com.fpoly.thainv.controllers;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fpoly.thainv.models.User_fake;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@Controller
public class LoginController {
	@Autowired
	HttpServletRequest req;
	
	@Autowired
	HttpSession session;
	
	@GetMapping("/do-login")
	public String login() {
		return "Admin/html/auth-login-basic";
	}
	
	@PostMapping("/do-login")
	public String doLogin(Model model) {
		boolean check = false;
		
		User_fake u = new User_fake("username1", "123321");
		
		String username =req.getParameter("userId");
		String password =req.getParameter("password");
		
		System.out.println(username);
		System.out.println(password);
		
		if (username.equalsIgnoreCase(u.getUsername()) && password.equalsIgnoreCase(u.getPassword())) {
			check =true;
			session.setAttribute("checkLogin", check);
			return "redirect:/home";
		} else {
			return "Admin/html/auth-login-basic";
		}
		
		
	}
}
