package com.cp.www.util.webSocket;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.server.standard.ServerEndpointExporter;

/**
 *
 *
 * @author cp
 * @version 创建时间：2020年1月10日 下午1:41:49
 */
@Configuration
public class WebSocketConfiguration {

	@Bean
	public ServerEndpointExporter serverEndpointExporter() {
		return new ServerEndpointExporter();
	}

}
