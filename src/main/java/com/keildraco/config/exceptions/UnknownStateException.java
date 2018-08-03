package com.keildraco.config.exceptions;

/**
 *
 * @author Daniel Hazelton
 *
 */
public class UnknownStateException extends Exception {

	/**
	 *
	 */
	private static final long serialVersionUID = 2984719809304083394L;

	/**
	 *
	 * @param mess
	 */
	public UnknownStateException(final String mess) {
		super(mess);
	}
}
