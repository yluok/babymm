package com.babymm.domain;

/**
 * 返回结果类
 * @author yluo0
 *
 */
public class Result {
	
	private boolean success;		//true成功，false失败或者错误
	private String message;			//附带消息
	
	
	public Result() {
		super();
	}
	public Result(boolean success, String message) {
		super();
		this.success = success;
		this.message = message;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	

}
