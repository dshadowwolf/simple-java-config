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
public final class NullParser extends AbstractParserBase {
	private static final String	NULLPARSER				= "NULLPARSER";

	private static boolean flag = false;

	/**
	 *
	 * @param factory
	 * @param parent
	 */
	public NullParser(final TypeFactory factory, final ParserInternalTypeBase parent) {
		super(factory, parent, NULLPARSER);
	}

	@Override
	public void registerTransitions(@Nullable final TypeFactory factory) {
		// blank
	}

	@Override
	public ParserInternalTypeBase getState(@Nonnull final Tokenizer tokenizer) {
		return EMPTY_TYPE;
	}

	public static boolean getFlag() {
		return flag;
	}

	public static void setFlag() {
		flag = true;
	}
}