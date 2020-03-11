package com.szh.wechat.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.szh.wechat.model.User;
import com.szh.wechat.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {

	@Autowired
	private UserService userService;

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@PostMapping("/logout")
	public String logout(@CookieValue("msgKey") String msgKey) {
		// userService.removeFromRedis(msgKey);
		return "login";
	}

	/**
	 * 登陆失败则返回到login.html或fail.html（fail则不牵扯到jsp的问题）；<br>
	 * 登陆成功则返回到当前用户拥有权限的第一个html页面，此页面一加载就立即ajax访问页面数据，包括菜单列表。
	 */
	@PostMapping("/doLogin")
	public void dispatchLogin(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		String account = req.getParameter("account");
		String password = req.getParameter("password");
		// 获取到的这么多user（同一账户）只是role或门店不同，密码等其他都一致
		User user = userService.selectByAccountAndPassword(account, password);
		if (user != null) {
			log.debug("用户{}登陆验证成功", user);
			// 登陆成功后，需要看此用户的信息是否存在于redis，存在则刷新过期时间，不存在则插入记录
			Cookie cookie = new Cookie("userId", user.getId() + "");
			// 负数代表浏览器关闭则删除cookie
			cookie.setMaxAge(-1);
			cookie.setPath("/");
			resp.addCookie(cookie);
			resp.setStatus(HttpStatus.FOUND.value());
			resp.setHeader("location", req.getContextPath() + "/main");
		} else {
			// 验证失败
			log.info("用户{}登陆验证失败", account);
			resp.setStatus(HttpStatus.FOUND.value());
			resp.setHeader("location", req.getContextPath() + "/fail");
			return;
		}
	}

	@GetMapping("/main")
	public String toMain() {
		return "main";
	}

	@GetMapping("/fail")
	public String toFail() {
		return "fail";
	}

}
