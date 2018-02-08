package com.webmvc.excepetion;

public class WebMVCException extends RuntimeException{

	private static final long serialVersionUID = 5760050537529222127L;

	public WebMVCException() {}
	
	public WebMVCException(String msg) {
		super(msg);
	}
	
	public WebMVCException(Exception e) {
		super(e);
	}
	
	public WebMVCException(String msg, Exception e) {
		super(msg, e);
	}
	
}
