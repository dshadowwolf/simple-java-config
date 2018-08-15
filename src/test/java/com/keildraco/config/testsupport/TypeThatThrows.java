package com.keildraco.config.testsupport;

import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

/**
 *
 * @author Daniel Hazelton
 *
 *
 */
public final class TypeThatThrows extends ParserInternalTypeBase {
	private static final String	TESTING_PURPOSES_ONLY	= "Testing purposes only";
	private static final String	ABSTRACT				= "Abstract!";

	/**
	 *
	 * @param parentIn
	 * @param nameIn
	 * @param valueIn
	 * @throws IllegalAccessException
	 */
	TypeThatThrows(final ParserInternalTypeBase parentIn, final String nameIn,
			final String valueIn) throws GenericParseException {
		super(parentIn, nameIn, valueIn);
		throw new GenericParseException(TESTING_PURPOSES_ONLY);
	}

	@Override
	public String getValue() {
		return ABSTRACT;
	}

	@Override
	public String getValueRaw() {
		return this.getValue();
	}
}