package com.fpoly.thainv.untils;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
/**
 * @author nhanprogrammer
 */
public class CookieUtil {
	public static void add(HttpServletResponse resp, String key, String value, int hours) {
		Cookie cookie = new Cookie(key, value);

		cookie.setPath("/");
		cookie.setMaxAge(hours*60*60);

		resp.addCookie(cookie);
	}

	public static String get(HttpServletRequest req, String key) {
		Cookie[] cookies = req.getCookies();
		System.out.println("Ke = "+key);
		System.out.println("Req = "+req);
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie.getName().equals(key)) {
					return cookie.getValue();
				}
			}
		}

		return "";
	}

	public static void clear(HttpServletResponse resp, String key) {
        Cookie cookie = new Cookie(key, null);
        cookie.setPath("/");
        cookie.setMaxAge(0); // Đặt thời gian sống là 0 để xóa cookie
        resp.addCookie(cookie);
    }
}
