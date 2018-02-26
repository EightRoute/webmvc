package com.webmvc.enums;

public enum RequestMethod {
	GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE;

	public static RequestMethod[] getAll(){
		RequestMethod[] methods = new RequestMethod[]{GET, HEAD, POST, PUT, PATCH, DELETE, OPTIONS, TRACE};
		return methods;
	}

	@Override
	public String toString() {
		return this.name();
	}
}
