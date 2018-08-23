package com.keildraco.config.testsupport;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;


public class BrokenParserState extends AbstractParserBase {

	public BrokenParserState(TypeFactory factoryIn, ParserInternalTypeBase parentIn,
			String nameIn) {
		super(factoryIn, parentIn, nameIn);
		throw new IllegalAccessError("blargh");
	}

	@Override
	public ParserInternalTypeBase getState(Tokenizer tokenizer) {
		return com.keildraco.config.Config.EMPTY_TYPE;
	}

	@Override
	public void registerTransitions(TypeFactory factory) {
		// nothing to register
	}

}
