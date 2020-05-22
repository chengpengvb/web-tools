package com.cp.www.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.cp.www.dto.ResultModel;
import com.cp.www.module.WebSshModule;
import com.cp.www.module.WebTelnetModule;

/**
 *
 *
 * @author cp
 * @version 创建时间：2020年1月6日 上午10:41:41
 */
@Controller
@RequestMapping("/webTool")
public class WebToolController {

	@RequestMapping(value = "/loginSsh")
	@ResponseBody
	public ResultModel loginSSH(String ip, int port, String user, String passWord) {
		return WebSshModule.login(ip, port, user, passWord);
	}

	@RequestMapping(value = "/closeSsh")
	@ResponseBody
	public ResultModel closeSSH(String id) {
		return WebSshModule.close(id);
	}

	@RequestMapping(value = "/loginTelnet")
	@ResponseBody
	public ResultModel loginTelnet(String ip, int port) {
		return WebTelnetModule.login(ip, port);
	}

	@RequestMapping(value = "/closeTelnet")
	@ResponseBody
	public ResultModel closeTelnet(String id) {
		return WebTelnetModule.close(id);
	}
}
