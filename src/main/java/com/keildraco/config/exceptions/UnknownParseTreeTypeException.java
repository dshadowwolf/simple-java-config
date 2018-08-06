package com.keildraco.config.exceptions;

/**
 *
 * @author Daniel Hazelton
 */
public class UnknownParseTreeTypeException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 4584871943362881729L;

	/**
	 *
	 * @param message
	 */
	public UnknownParseTreeTypeException(final String message) {
		super(message);
	}
}
