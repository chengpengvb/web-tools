package com.cp.www.module.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.UnknownHostException;

import org.apache.commons.net.DefaultSocketFactory;
import org.apache.commons.net.telnet.EchoOptionHandler;
import org.apache.commons.net.telnet.SuppressGAOptionHandler;
import org.apache.commons.net.telnet.TelnetClient;
import org.apache.commons.net.telnet.TerminalTypeOptionHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cp.www.dto.ResultModel;
import com.cp.www.util.Callback;
/**
*
*
* @author cp
* @version 创建时间：2020年1月10日 下午3:05:25
*/
public class JavaTelnet implements Runnable {
	private Logger logger = LogManager.getLogger();
	private String ip;
	private int timeOut = 4000;
	private TelnetClient tc = null;

	private Socket socket;

	private Callback callback;

	public void close() {
		InputStream is = tc.getInputStream();
		try {
			if (is != null) {
				is.close();
			}
			if (tc != null && tc.isConnected()) {
				tc.disconnect();
			}
		} catch (Exception e) {
		}
		logger.warn("JavaTelnet[{}] close.", ip);
	}

	public void run() {
		if (tc != null && tc.getInputStream() != null) {
			InputStream is = tc.getInputStream();
			String echo = readOneEcho(is);
			while (echo != null) {
				if (callback != null) {
					callback.doCallback(null, echo);
				}
				echo = readOneEcho(is);
			}
		}
	}

	private String readOneEcho(InputStream instr) {
		if (instr == null) {
			return null;
		}
		byte[] buff = new byte[1024];
		int ret_read = 0;
		try {
			ret_read = instr.read(buff);
		} catch (IOException e) {
			return null;
		}
		if (ret_read > 0) {
			return new String(buff, 0, ret_read);
		} else {
			return null;
		}
	}

	public void sendCmd(String command) {
		try {
			OutputStream outstr = tc.getOutputStream();
			outstr.write(command.getBytes());
			outstr.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public ResultModel login(String ip, int port) {
		this.ip = ip;
		if (tc == null) {
			tc = new TelnetClient();
		}
		try {
			// 连接配置
			TerminalTypeOptionHandler ttopt = new TerminalTypeOptionHandler("VT100", false, false, true, false);
			EchoOptionHandler echoopt = new EchoOptionHandler(true, true, true, true);
			SuppressGAOptionHandler gaopt = new SuppressGAOptionHandler(false, false, false, false);
			tc.addOptionHandler(ttopt);
			tc.addOptionHandler(echoopt);
			tc.addOptionHandler(gaopt);
		} catch (Throwable e) {
			return new ResultModel("登录失败", false, e.getMessage());
		}
		// 连接
		try {
			DefaultSocketFactory factory = new DefaultSocketFactory() {
				public Socket createSocket(String host, int port) throws UnknownHostException, IOException {
					socket = new Socket();
					socket.connect(new InetSocketAddress(Inet4Address.getByName(host), port), timeOut);
					return socket;
				}
			};
			tc.setSocketFactory(factory);
			tc.connect(ip, port);
			logger.info("JavaTelnet LOGIN[{}] SUCCESS.");
			return new ResultModel("链接成功", true, null);
		} catch (Throwable e) {
			logger.warn("JavaTelnet LOGIN[{}] ERROR:{}.", e.getMessage());
			return new ResultModel("登录失败", false, e.getMessage());
		}
	}

	public String getIp() {
		return ip;
	}

	public void startTelnet() {
		new Thread(this).start();
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

}
