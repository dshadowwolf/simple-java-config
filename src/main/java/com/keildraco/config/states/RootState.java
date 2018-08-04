package com.keildraco.config.states;

import com.keildraco.config.data.TokenType;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

import javax.annotation.Nullable;

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
	public RootState(@Nullable final TypeFactory factoryIn, @Nullable final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "ROOT");
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(this.getName(),
				TokenType.IDENTIFIER, TokenType.OPEN_BRACE, "SECTION");
		factory.registerStateTransition(this.getName(),
				TokenType.IDENTIFIER, TokenType.STORE, "KEYVALUE");
	}
}
