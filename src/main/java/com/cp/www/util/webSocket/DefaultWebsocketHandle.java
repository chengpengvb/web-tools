package com.cp.www.util.webSocket;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.websocket.OnClose;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.PathParam;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 *
 * @author cp
 * @version 创建时间：2020年1月6日 上午11:03:32
 */
public abstract class DefaultWebsocketHandle implements WebSocketHandle {

	protected Logger logger = LogManager.getLogger();

	private Map<String, Session> sessions = new ConcurrentHashMap<>();

	@OnOpen
	public void onOpen(Session session, @PathParam("uuid") String uuid) {
		sessions.put(session.getId(), session);
		logger.info("WebSocket onOpen:{}.", session.getId());
		openHandle(session, uuid);
	}

	@OnClose
	public void onClose(Session session) {
		sessions.remove(session.getId());
		logger.info("WebSocket onClose:{}.", session.getId());
		closeHandle(session);
	}

	public Session getSession(String id) {
		return sessions.get(id);
	}

	@OnMessage
	public void onMessage(String message, Session session) {
		onMessageHandle(message, session);
	}

	public synchronized void sendTextMsg(String sessionId, String message) {
		Session session = sessions.get(sessionId);
		try {
			session.getBasicRemote().sendText(message);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void sendBinaryMsg(String sessionId, ByteBuffer data) {
		Session session = sessions.get(sessionId);
		session.getAsyncRemote().sendBinary(data);
	}
}
