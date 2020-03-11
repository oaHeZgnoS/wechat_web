package com.szh.wechat.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import springfox.documentation.annotations.ApiIgnore;

@ApiIgnore
@Controller
public class HomeController {

    @RequestMapping("swagger")
    public String swagger() {
        return "redirect:swagger-ui.html";
    }

    @RequestMapping("/")
	public String home() {
		return "index.html";
	}

}
