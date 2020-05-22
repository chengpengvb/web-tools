package com.cp.www.module;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.cp.www.dto.ResultModel;
import com.cp.www.module.impl.JavaSsh;
import com.cp.www.util.Callback;
import com.cp.www.util.IdFactory;
import com.cp.www.util.webSocket.DefaultWebsocketHandle;
/**
*
*
* @author cp
* @version 创建时间：2019年12月26日 下午3:49:25
*/
@ServerEndpoint("/autoSshSocket/{uuid}")
@Component
public class WebSshModule extends DefaultWebsocketHandle {

	private static Map<String, Config> configs = new ConcurrentHashMap<>();// 由于多例问题所以创建为静态static

	private final static int TIME_OUT = 1000 * 60 * 2;// 2分钟不操作关闭连接

	static class Config {
		String uuid;
		JavaSsh javaSsh;
		Session session;

		public String getUuid() {
			return uuid;
		}

		public Config setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		public JavaSsh getJavaSsh() {
			return javaSsh;
		}

		public Config setJavaSsh(JavaSsh javaSsh) {
			this.javaSsh = javaSsh;
			return this;
		}

	}

	@Override
	public void openHandle(Session session, String uuid) {
		session.setMaxIdleTimeout(TIME_OUT);
		Config config = configs.get(uuid);
		if (config == null) {
			logger.info("get javaSsh by uuid[{}] is null!.", uuid);
			return;
		}
		config.session = session;
		JavaSsh javaSsh = config.javaSsh;
		Callback callback = new Callback() {
			@Override
			public void doCallback(Object param, Object msg) {
				sendTextMsg(session.getId(), msg.toString());
			}
		};
		javaSsh.setCallback(callback);
		javaSsh.startSsh();
		logger.info("ssh[{}] Login SUCCESS.", javaSsh.getIp());
	}

	public String getConfig(String sessionId) {
		Collection<Config> values = configs.values();
		for (Config config : values) {
			if (config.session.getId().equals(sessionId)) {
				return config.getUuid();
			}
		}
		return null;
	}

	@Override
	public void closeHandle(Session session) {
		String uuid = getConfig(session.getId());
		if (uuid == null) {
			logger.info("javaSsh[{}] fail obj does not exist!.");
			return;
		}
		JavaSsh javaSsh = configs.get(uuid).javaSsh;
		javaSsh.close();
		configs.remove(uuid);
		logger.info("ssh[{}] close.", javaSsh.getIp());
	}

	@Override
	public void onMessageHandle(String message, Session session) {
		String uuid = getConfig(session.getId());
		if (uuid == null) {
			logger.warn("session[{}] does not exist!", session.getId());
			return;
		}
		configs.get(uuid).javaSsh.sendCmd(message);
	}

	public static ResultModel login(String ip, int port, String user, String passWord) {
		JavaSsh javaSsh = new JavaSsh();
		ResultModel resultModel = javaSsh.login(ip, port, user, passWord);
		if (resultModel.isOk()) {
			String buildId = IdFactory.buildId();
			resultModel.setObj(buildId);
			Config config = new Config();
			config.setUuid(buildId).setJavaSsh(javaSsh);
			configs.put(buildId, config);
		}
		return resultModel;
	}

	public static ResultModel close(String id) {
		Config config = configs.get(id);
		if (config == null) {
			return new ResultModel("对象已经关闭或不存在", false, null);
		}
		JavaSsh javaSsh = config.javaSsh;
		javaSsh.close();
		try {
			config.session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		configs.remove(id);
		return new ResultModel("退出登录", true, null);
	}
}
