package com.keildraco.config.states;

import java.util.Locale;

import com.keildraco.config.data.TokenType;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class RootState extends AbstractParserBase {

	/**
	 *
	 * @param factoryIn
	 * @param parentIn
	 */
	public RootState(final TypeFactory factoryIn, final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "ROOT");
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(this.getName().toUpperCase(Locale.ENGLISH),
				TokenType.IDENTIFIER, TokenType.OPEN_BRACE, "SECTION");
		factory.registerStateTransition(this.getName().toUpperCase(Locale.ENGLISH),
				TokenType.IDENTIFIER, TokenType.STORE, "KEYVALUE");
	}

}
