package com.cp.www.module.impl;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.cp.www.dto.ResultModel;
import com.cp.www.util.Callback;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UIKeyboardInteractive;
import com.jcraft.jsch.UserInfo;
/**
*
*
* @author cp
* @version 创建时间：2020年3月18日 上午11:56:58
*/
public class JavaSsh implements Runnable {
	private Logger logger = LogManager.getLogger();

	private JSch jsch = null;
	private Session session;
	private Channel channel;
	private Callback callback;
	private OutputStream os;
	private InputStream is;
	private String ip;

	@Override
	public void run() {
		String echo = readOneEcho(is);
		while (echo != null) {
			if (callback != null) {
				callback.doCallback(null, echo);
			}
			echo = readOneEcho(is);
		}
	}

	protected String readOneEcho(InputStream instr) {
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

	public void close() {
		if (session != null) {
			session.disconnect();
		}
		if (channel != null) {
			channel.disconnect();
		}
	}

	public ResultModel login(String ip, int port, String user, String passWord) {
		jsch = new JSch();
		try {
			session = jsch.getSession(user, ip, port);
			session.setPassword(passWord);
			UserInfo ui = new SSHUserInfo() {
				public void showMessage(String message) {
				}

				public boolean promptYesNo(String message) {
					return true;
				}
			};
			session.setUserInfo(ui);
			session.connect(30000);
			channel = session.openChannel("shell");
			channel.connect(3000);
			is = channel.getInputStream();
			os = channel.getOutputStream();
			this.ip = ip;
			return new ResultModel("登录成功", true, null);
		} catch (Exception e) {
			logger.error("ssh login error {}.", e.getMessage());
			return new ResultModel("登录失败", false, e.getMessage());
		}
	}

	public void startSsh() {
		new Thread(this).start();
	}

	public void sendCmd(String command) {
		try {
			os.write(command.getBytes());
			os.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private abstract class SSHUserInfo implements UserInfo, UIKeyboardInteractive {
		public String getPassword() {
			return null;
		}

		public boolean promptYesNo(String str) {
			return true;
		}

		public String getPassphrase() {
			return null;
		}

		public boolean promptPassphrase(String message) {
			return true;
		}

		public boolean promptPassword(String message) {
			return true;
		}

		public void showMessage(String message) {
		}

		public String[] promptKeyboardInteractive(String destination, String name, String instruction, String[] prompt,
				boolean[] echo) {
			return null;
		}
	}

	public String getIp() {
		return ip;
	}

	public void setCallback(Callback callback) {
		this.callback = callback;
	}

}
