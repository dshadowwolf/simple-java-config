package com.keildraco.config.states;

import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.Tokenizer;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.factory.Tokenizer.Token;
import com.keildraco.config.factory.Tokenizer.TokenType;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.SectionType;

public class SectionParser extends AbstractParserBase implements IStateParser {

	public SectionParser(TypeFactory factoryIn, ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "SECTION");
	}

	@Override
	public ParserInternalTypeBase getState(Tokenizer tok) throws IllegalParserStateException, UnknownStateException, GenericParseException {
		if(!tok.hasNext()) throw new IllegalParserStateException("End of input at start of state");

		String sectionName = tok.nextToken().getValue();
		tok.nextToken(); // skip the OPEN_BRACE
		
		Token current = tok.peek();
		Token next = tok.peekToken();
		
		SectionType rv = new SectionType(sectionName);
		
		while(tok.hasNext()) {
			if(current.getType() == TokenType.CLOSE_BRACE) {
				tok.nextToken();
				rv.setName(sectionName); // force this, despite what other code thinks
				return rv;
			}
			rv.addItem(this.factory.nextState(this.name.toUpperCase(), current, next).getState(tok));
			current = tok.peek();
			next = tok.peekToken();
		}
		
		throw new GenericParseException("End of input while parsing a SECTION");
	}

	@Override
	public void registerTransitions(TypeFactory factory) {
		factory.registerStateTransition(this.getName().toUpperCase(), TokenType.IDENTIFIER, TokenType.STORE, "KEYVALUE");
		factory.registerStateTransition(this.getName().toUpperCase(), TokenType.IDENTIFIER, TokenType.OPEN_BRACE, this.getName().toUpperCase());
	}

}
