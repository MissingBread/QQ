package com.cbs.view;
/**
 * 信息的包装类
 * @author CBS
 *
 */
public class Msg {
	private String MyUID;
	private String toUID;
	private String msg;
	private String code;
	private String type;
	public String getMyUID() {
		return MyUID;
	}
	public void setMyUID(String myUID) {
		MyUID = myUID;
	}
	public String getToUID() {
		return toUID;
	}
	public void setToUID(String toUID) {
		this.toUID = toUID;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
}
