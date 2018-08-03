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
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;

public final class KeyValueParser extends AbstractParserBase {

	public KeyValueParser(final TypeFactory factoryIn, final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "KEYVALUE");
	}

	@Override
	public ParserInternalTypeBase getState(final Tokenizer tok)
			throws IllegalParserStateException, UnknownStateException, GenericParseException {
		if (!tok.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		String key = tok.nextToken().getValue();
		tok.nextToken();

		Token next = tok.peek();
		Token following = tok.peekToken();

		if (next.getType() == TokenType.IDENTIFIER
				&& (following == null || following.getType() != TokenType.OPEN_PARENS)) {
			ParserInternalTypeBase rv = this.factory.getType(null, key, next.getValue(),
					ItemType.IDENTIFIER);
			tok.nextToken();
			return rv;
		}

		IStateParser parser = this.factory.nextState(this.getName().toUpperCase(), next, following);
		ParserInternalTypeBase rv = parser.getState(tok);
		rv.setName(key);
		return rv;
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(this.getName().toUpperCase(), TokenType.OPEN_LIST,
				TokenType.IDENTIFIER, "LIST");
	}

}
