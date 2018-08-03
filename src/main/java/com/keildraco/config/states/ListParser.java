package com.keildraco.config.states;

import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.ListType;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class ListParser extends AbstractParserBase {

	/**
	 *
	 * @param factoryIn
	 * @param parentIn
	 */
	public ListParser(final TypeFactory factoryIn, final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "LIST");
	}

	@Override
	public ParserInternalTypeBase getState(final Tokenizer tok)
			throws IllegalParserStateException, UnknownStateException, GenericParseException {
		if (!tok.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		// we should enter with OPEN_LIST IDENTIFIER
		// so first we consume one token (OPEN_LIST)
		tok.nextToken();

		// next we get our current Token and the token that follows
		Token current = tok.peek();
		Token next = tok.peekToken();

		ListType rv = new ListType("");

		while (tok.hasNext()) {
			switch (current.getType()) {
				case IDENTIFIER:
					if (next != null && (next.getType() != TokenType.SEPERATOR
							&& next.getType() != TokenType.CLOSE_LIST)) {
						rv.addItem(
								this.getFactory().nextState(this.getName().toUpperCase(), current, next)
										.getState(tok));
					} else {
						rv.addItem(this.getFactory().getType(null, current.getValue(),
								current.getValue(), ItemType.IDENTIFIER));
						tok.nextToken(); // consume the identifier
					}
					break;
				case SEPERATOR:
					tok.nextToken(); // consume!
					break;
				case CLOSE_LIST:
					tok.nextToken(); // consume!
					return rv;
				default:
					throw new GenericParseException(String.format(
							"Odd, this (token of type %s, value %s) should not be here!",
							current.getType(), current.getValue()));
			}
			current = tok.peek();
			next = tok.peekToken();
		}

		throw new GenericParseException("End of input found while processing a LIST!");
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(this.getName().toUpperCase(), TokenType.IDENTIFIER,
				TokenType.OPEN_PARENS, "OPERATION");
	}
}
