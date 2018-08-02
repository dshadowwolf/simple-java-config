package com.keildraco.config.states;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.factory.Tokenizer.TokenType;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

public class RootState extends AbstractParserBase implements IStateParser {

	public RootState(TypeFactory factoryIn, ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "ROOT");
	}

	@Override
	public void registerTransitions(TypeFactory factory) {
		factory.registerStateTransition(this.getName().toUpperCase(), TokenType.IDENTIFIER, TokenType.OPEN_BRACE, "SECTION");
		factory.registerStateTransition(this.getName().toUpperCase(), TokenType.IDENTIFIER, TokenType.STORE, "KEYVALUE");
	}

}
