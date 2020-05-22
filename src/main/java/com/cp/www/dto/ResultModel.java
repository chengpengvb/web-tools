package com.cp.www.dto;

import java.io.Serializable;

/**
 *
 *
 * @author cp
 * @version 创建时间：2019年12月27日 下午3:04:22
 */
public class ResultModel implements Serializable {
	private static final long serialVersionUID = 1L;
	public static final int CODE_SUCCESS = 200;
	public static final int CODE_ERROR = 500;
	public static final int CODE_NOT_FIND = 404;

	private String msg;
	private int code = CODE_SUCCESS;
	private Object obj;

	public ResultModel(String msg, int code, Object obj) {
		this.msg = msg;
		this.code = code;
		this.obj = obj;
	}

	public ResultModel(String msg, boolean ok, Object obj) {
		this.msg = msg;
		this.obj = obj;
		if (ok) {
			code = CODE_SUCCESS;
		} else {
			code = CODE_ERROR;
		}
	}

	public boolean isOk() {
		return code == CODE_SUCCESS ? true : false;
	}

	public ResultModel() {

	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

}
