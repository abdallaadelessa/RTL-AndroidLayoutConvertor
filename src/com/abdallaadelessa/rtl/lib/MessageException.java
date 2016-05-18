package com.abdallaadelessa.rtl.lib;

public class MessageException extends Exception {
	private String code;

	public MessageException(String message) {
		this("", message);
	}

	public MessageException(String code, String message) {
		super(message);
		this.code = code;
	}

}
