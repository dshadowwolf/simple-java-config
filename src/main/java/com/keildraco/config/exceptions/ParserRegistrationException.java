package com.keildraco.config.exceptions;


public class ParserRegistrationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5778693947692239386L;

	public ParserRegistrationException(final String parserName) {
		super("An issue occurred while registering parser " + parserName);
	}
}
