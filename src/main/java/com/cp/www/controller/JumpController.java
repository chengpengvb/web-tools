package com.cp.www.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 *
 *
 * @author cp
 * @version 创建时间：2019年12月26日 上午11:15:09
 */
@Controller
public class JumpController {

	@RequestMapping
	public String defaultPage() {
		return "index";
	}

	@RequestMapping(value = "/jumpConsole")
	public String jumpConsole() {
		return "console";
	}

}
