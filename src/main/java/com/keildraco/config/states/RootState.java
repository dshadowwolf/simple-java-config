package com.keildraco.config.states;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.factory.Tokenizer.TokenType;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

public final class RootState extends AbstractParserBase implements IStateParser {

	public RootState(final TypeFactory factoryIn, final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "ROOT");
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(this.getName().toUpperCase(), TokenType.IDENTIFIER,
				TokenType.OPEN_BRACE, "SECTION");
		factory.registerStateTransition(this.getName().toUpperCase(), TokenType.IDENTIFIER,
				TokenType.STORE, "KEYVALUE");
	}

}
