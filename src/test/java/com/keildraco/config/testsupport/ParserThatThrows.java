package com.keildraco.config.testsupport;

import static com.keildraco.config.Config.EMPTY_TYPE;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class ParserThatThrows extends AbstractParserBase {
	private static final String	TEST					= "TEST";
	private static final String	TESTING_PURPOSES_ONLY	= "Testing purposes only";

	/**
	 *
	 * @param factory
	 * @param parent
	 * @throws IllegalAccessException
	 */
	ParserThatThrows(final TypeFactory factory, final ParserInternalTypeBase parent)
			throws IllegalArgumentException {
		super(factory, parent, TEST);
		throw new IllegalArgumentException(TESTING_PURPOSES_ONLY);
	}

	@Override
	public void registerTransitions(@Nullable final TypeFactory factory) {
		// not needed
	}

	@Override
	public ParserInternalTypeBase getState(@Nonnull final Tokenizer tokenizer) {
		return EMPTY_TYPE;
	}
}