package com.ge.ceed.domeapi.web;

/**
 * Data object used to organize a standard response via JSON in each endpoint
 *
 */
public class Response {
	public static final String SUCCESS = "success";
	public static final String ERROR = "error";

	private static final String UNKNOWN_ERROR = "Unknown Error";
	private String status;
	private String msg;
	private Object pkg;
	
	public Response() {
		status = ERROR;
		msg = UNKNOWN_ERROR;
		pkg = null;
	}
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getPkg() {
		return pkg;
	}

	public void setPkg(Object pkg) {
		this.pkg = pkg;
	}

}
