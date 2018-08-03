package com.keildraco.config.states;

import java.util.Locale;

import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class KeyValueParser extends AbstractParserBase {

	/**
	 *
	 * @param factoryIn
	 * @param parentIn
	 */
	public KeyValueParser(final TypeFactory factoryIn, final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "KEYVALUE");
	}

	@Override
	public ParserInternalTypeBase getState(final Tokenizer tok)
			throws IllegalParserStateException, UnknownStateException, GenericParseException {
		if (!tok.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		final String key = tok.nextToken().getValue();
		tok.nextToken();

		final Token next = tok.peek();
		final Token following = tok.peekToken();

		if (next.getType() == TokenType.IDENTIFIER
				&& (following == null || following.getType() != TokenType.OPEN_PARENS)) {
			final ParserInternalTypeBase rv = this.getFactory().getType(null, key, next.getValue(),
					ItemType.IDENTIFIER);
			tok.nextToken();
			return rv;
		}

		final IStateParser parser = this.getFactory()
				.nextState(this.getName().toUpperCase(Locale.ENGLISH), next, following);
		final ParserInternalTypeBase rv = parser.getState(tok);
		rv.setName(key);
		return rv;
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(this.getName().toUpperCase(Locale.ENGLISH),
				TokenType.OPEN_LIST, TokenType.IDENTIFIER, "LIST");
	}

}
