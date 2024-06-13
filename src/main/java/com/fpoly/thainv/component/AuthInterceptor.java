package com.fpoly.thainv.component;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import com.fpoly.thainv.entities.Users;
import com.fpoly.thainv.jpa.AuthJpa;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthInterceptor implements HandlerInterceptor {
    @Autowired
    private AuthJpa authJpa;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (request.getCookies() != null) {
            String username = null, password = null;
            for (Cookie cookie : request.getCookies()) {
                if ("email".equals(cookie.getName())) {
                    username = cookie.getValue();
                } else if ("password".equals(cookie.getName())) {
                    password = cookie.getValue();
                }
            }

            if (username != null && password != null) {
                Optional<Users> userOptional = authJpa.findByEmail(username);
                if (userOptional.isPresent()) {
                    Users userEntity = userOptional.get();
                    if (userEntity.getPassword().equals(password)) { 
                        return true;
                    }
                }
            }
        }

        response.sendRedirect(String.format("/login?path=%s", request.getRequestURI()));
        return false;
    }
}