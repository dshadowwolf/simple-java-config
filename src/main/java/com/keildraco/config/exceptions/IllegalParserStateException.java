package com.keildraco.config.exceptions;

/**
 *
 * @author Daniel Hazelton
 *
 */
public class IllegalParserStateException extends RuntimeException {
	/**
	 *
	 */
	private static final long serialVersionUID = -3178889090285085141L;

	/**
	 *
	 * @param message
	 */
	public IllegalParserStateException(final String message) {
		super(message);
	}
}
