package com.keildraco.config.exceptions;

import com.keildraco.config.data.ItemType;

/**
 *
 * @author Daniel Hazelton
 */
public class TypeRegistrationException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = -1538778272205968570L;

	/**
	 *
	 * @param type
	 */
	public TypeRegistrationException(final ItemType type) {
		super("An issue occurred while registering type " + type);
	}

	/**
	 *
	 * @param message
	 */
	public TypeRegistrationException(final String message) {
		super(message);
	}

}
