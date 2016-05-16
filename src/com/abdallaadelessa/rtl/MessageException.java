package com.abdallaadelessa.rtl;

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
