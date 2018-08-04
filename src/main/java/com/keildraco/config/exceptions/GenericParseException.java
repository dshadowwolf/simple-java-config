package com.keildraco.config.exceptions;

/**
 *
 * @author Daniel Hazelton
 *
 */
public class GenericParseException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -3014268460892028738L;

	/**
	 *
	 * @param mess
	 */
	public GenericParseException(final String mess) {
		super(mess);
	}
}
