package com.keildraco.config.exceptions;

public class UnknownStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2984719809304083394L;

	public UnknownStateException() {
		super("Parser has no transition for the current state!");
	}

	public UnknownStateException(String message) {
		super(message);
	}

	public UnknownStateException(Throwable cause) {
		super(cause);
	}

	public UnknownStateException(String message, Throwable cause) {
		super(message, cause);
	}

	public UnknownStateException(String message, Throwable cause, boolean enableSuppression,
			boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);
	}

}
