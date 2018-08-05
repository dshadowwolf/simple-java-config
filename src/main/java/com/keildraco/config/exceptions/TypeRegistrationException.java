package com.keildraco.config.exceptions;

import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;

public class TypeRegistrationException extends RuntimeException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1538778272205968570L;

	public TypeRegistrationException(final ItemType type) {
		super("An issue occurred while registering type " + type);
	}
}
