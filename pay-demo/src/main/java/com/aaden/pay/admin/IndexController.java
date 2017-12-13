package com.aaden.pay.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *  @Description 首页
 *  @author aaden
 *  @date 2017年12月29日
 */
@Controller
@Scope("prototype")
public class IndexController {

	@RequestMapping(value = { "/", "index" })
	public String toIdenx(HttpServletRequest req) {
		return "index";
	}

	@RequestMapping("readme")
	public String main(HttpServletRequest req) {
		return "readme";
	}

}
