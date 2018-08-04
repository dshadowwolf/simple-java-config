package com.keildraco.config.exceptions;

/**
 *
 * @author Daniel Hazelton
 *
 */
public class UnknownStateException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 2984719809304083394L;

	/**
	 *
	 * @param message
	 */
	public UnknownStateException(final String message) {
		super(message);
	}
}
