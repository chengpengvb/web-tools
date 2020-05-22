package com.cp.www.dto;

/**
 *
 *
 * @author cp
 * @version 创建时间：2020年3月18日 下午12:06:58
 */
public class ConfigDTO {
	private final String serviceIp;
	private final int servicePort;
	private final String theme;

	public ConfigDTO(String serviceIp, int servicePort, String theme) {
		this.serviceIp = serviceIp;
		this.servicePort = servicePort;
		this.theme = theme;
	}

	public String getServiceIp() {
		return serviceIp;
	}

	public int getServicePort() {
		return servicePort;
	}

	public String getTheme() {
		return theme;
	}

}
