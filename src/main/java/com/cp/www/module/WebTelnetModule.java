package com.cp.www.module;

import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

import org.springframework.stereotype.Component;

import com.cp.www.dto.ResultModel;
import com.cp.www.module.impl.JavaTelnet;
import com.cp.www.util.Callback;
import com.cp.www.util.IdFactory;
import com.cp.www.util.webSocket.DefaultWebsocketHandle;

/**
*
*
* @author cp
* @version 创建时间：2019年12月26日 下午4:11:40
*/
@ServerEndpoint("/autoTelnetSocket/{uuid}")
@Component
public class WebTelnetModule extends DefaultWebsocketHandle {

	private static Map<String, Config> configs = new ConcurrentHashMap<>();// 由于多例问题所以创建为静态static

	private final static int TIME_OUT = 1000 * 60 * 2;// 2分钟不操作关闭连接

	static class Config {
		String uuid;
		JavaTelnet javaTelnet;
		Session session;

		public Config setUuid(String uuid) {
			this.uuid = uuid;
			return this;
		}

		public Config setJavaTelnet(JavaTelnet javaTelnet) {
			this.javaTelnet = javaTelnet;
			return this;
		}
	}

	@Override
	public void openHandle(Session session, String uuid) {
		session.setMaxIdleTimeout(TIME_OUT);
		Config config = configs.get(uuid);
		if (config == null) {
			logger.info("get javaTelnet by uuid[{}] is null!.", uuid);
			return;
		}
		config.session = session;
		JavaTelnet javaTelnet = config.javaTelnet;
		Callback callback = new Callback() {
			@Override
			public void doCallback(Object param, Object msg) {
				sendTextMsg(session.getId(), msg.toString());
			}
		};
		javaTelnet.setCallback(callback);
		javaTelnet.startTelnet();
		logger.info("javaTelnet [{}] Login SUCCESS.", javaTelnet.getIp());
	}

	public String getConfig(String sessionId) {
		Collection<Config> values = configs.values();
		for (Config config : values) {
			if (config.session.getId().equals(sessionId)) {
				return config.uuid;
			}
		}
		return null;
	}

	@Override
	public void closeHandle(Session session) {
		String uuid = getConfig(session.getId());
		if (uuid == null) {
			logger.info("javaTelnet[{}] fail obj does not exist!.");
			return;
		}
		JavaTelnet javaTelnet = configs.get(uuid).javaTelnet;
		javaTelnet.close();
		configs.remove(uuid);
		logger.info("ssh[{}] close.", javaTelnet.getIp());
	}

	@Override
	public void onMessageHandle(String message, Session session) {
		String uuid = getConfig(session.getId());
		if (uuid == null) {
			logger.warn("session[{}] does not exist!", session.getId());
			return;
		}
		configs.get(uuid).javaTelnet.sendCmd(message);
	}

	public static ResultModel login(String ip, int port) {
		JavaTelnet javaTelnet = new JavaTelnet();
		ResultModel resultModel = javaTelnet.login(ip, port);
		if (resultModel.isOk()) {
			String buildId = IdFactory.buildId();
			resultModel.setObj(buildId);
			Config config = new Config();
			config.setUuid(buildId).setJavaTelnet(javaTelnet);
			configs.put(buildId, config);
		}
		return resultModel;
	}

	public static ResultModel close(String id) {
		Config config = configs.get(id);
		if (config == null) {
			return new ResultModel("对象已经关闭或不存在", false, null);
		}
		JavaTelnet javaTelnet = config.javaTelnet;
		javaTelnet.close();
		try {
			config.session.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		configs.remove(id);
		return new ResultModel("退出登录", true, null);
	}

}
