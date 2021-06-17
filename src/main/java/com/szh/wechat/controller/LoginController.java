package com.szh.wechat.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.google.code.kaptcha.Constants;
import com.google.code.kaptcha.impl.DefaultKaptcha;
import com.szh.wechat.model.User;
import com.szh.wechat.service.UserService;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class LoginController {

	@Autowired
	private UserService userService;
	
	@Autowired
	private DefaultKaptcha defaultKaptcha;

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

	@RequestMapping(value = "/captcha")
	public void getKaptchaImage(HttpServletRequest request, HttpServletResponse response) throws Exception {
		// 用字节数组存储
		byte[] captchaChallengeAsJpeg = null;
		ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
		ServletOutputStream responseOutputStream = response.getOutputStream();
		final HttpSession httpSession = request.getSession();
		try {
			// 生产验证码字符串并保存到session中
			String createText = defaultKaptcha.createText();
			// 打印随机生成的字母和数字
			log.debug(createText);
			httpSession.setAttribute(Constants.KAPTCHA_SESSION_KEY, createText);
			// 使用生产的验证码字符串返回一个BufferedImage对象并转为byte写入到byte数组中
			BufferedImage challenge = defaultKaptcha.createImage(createText);
			ImageIO.write(challenge, "jpg", jpegOutputStream);
			captchaChallengeAsJpeg = jpegOutputStream.toByteArray();
			response.setHeader("Cache-Control", "no-store");
			response.setHeader("Pragma", "no-cache");
			response.setDateHeader("Expires", 0);
			response.setContentType("image/jpeg");
			// 定义response输出类型为image/jpeg类型，使用response输出流输出图片的byte数组
			responseOutputStream.write(captchaChallengeAsJpeg);
			responseOutputStream.flush();
		} catch (IllegalArgumentException e) {
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return;
		} finally {
			responseOutputStream.close();
		}
	}

	@RequestMapping(value = "/checkcode", method = RequestMethod.POST, produces = "text/html; charset=utf-8")
	public boolean checkcode(HttpServletRequest request) {
		String captchaId = (String) request.getSession().getAttribute(Constants.KAPTCHA_SESSION_KEY);
		String parameter = request.getParameter("veritycode");
		System.out.println("Session  vrifyCode " + captchaId + " form veritycode " + parameter);
		if (!captchaId.equals(parameter)) {
			log.debug("验证码错误");
			return false;
		} else {
			log.debug("登录成功");
			return true;
		}
	}

}
