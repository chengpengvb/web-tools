package com.cp.www.util.webSocket;

import javax.websocket.Session;

/**
 *
 *
 * @author cp
 * @version 创建时间：2020年1月6日 上午10:21:21
 */
public interface WebSocketHandle {

	void closeHandle(Session session);

	void openHandle(Session session, String uuid);

	void onMessageHandle(String message, Session session);

}
