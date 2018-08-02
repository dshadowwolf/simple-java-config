package com.keildraco.config.interfaces;

import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.Tokenizer;
import com.keildraco.config.factory.TypeFactory;

public interface IStateParser {

	void setFactory(TypeFactory factory);

	TypeFactory getFactory();

	ParserInternalTypeBase getState(Tokenizer tok)
			throws IllegalParserStateException, UnknownStateException, GenericParseException;

	void setParent(ParserInternalTypeBase parent);

	ParserInternalTypeBase getParent();

	void setName(String name);

	String getName();

	void registerTransitions(TypeFactory factory);
}
