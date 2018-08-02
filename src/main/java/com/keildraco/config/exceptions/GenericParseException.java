package com.keildraco.config.exceptions;

public class GenericParseException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3014268460892028738L;

	public GenericParseException() {
	}

	public GenericParseException(String arg0) {
		super(arg0);
	}

	public GenericParseException(Throwable arg0) {
		super(arg0);
	}

	public GenericParseException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public GenericParseException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
