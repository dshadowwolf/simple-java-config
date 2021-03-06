package com.keildraco.config.states;

import javax.annotation.Nullable;

import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.ListType;
import static com.keildraco.config.data.Constants.ParserNames.LIST;
import static com.keildraco.config.data.Constants.ParserNames.OPERATION;

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
	public ListParser(final TypeFactory factoryIn,
			@Nullable final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, LIST);
	}

	private void handleIdentifierLoad(final Token next, final Token current,
			final Tokenizer tokenizer, final ListType rv) {
		if ((!next.isEmpty()) && ((next.getType() != TokenType.SEPERATOR)
				&& (next.getType() != TokenType.CLOSE_LIST))) {
			final IStateParser nextState = this.getFactory().nextState(this.getName(), current,
					next);
			rv.addItem(nextState.getState(tokenizer));
		} else {
			rv.addItem(this.getFactory().getType(null, current.getValue(), current.getValue(),
					ItemType.IDENTIFIER));
			tokenizer.nextToken(); // consume the identifier
		}
	}

	@Override
	public ParserInternalTypeBase getState(final Tokenizer tokenizer) {
		if (!tokenizer.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		// we should enter with OPEN_LIST IDENTIFIER
		// so first we consume one token (OPEN_LIST)
		tokenizer.nextToken();

		// next we get our current Token and the token that follows
		Token current = tokenizer.peek();
		Token next = tokenizer.peekToken();

		final ListType rv = new ListType("");

		while (tokenizer.hasNext()) {
			if (current.getType() == TokenType.IDENTIFIER) {
				this.handleIdentifierLoad(next, current, tokenizer, rv);
			} else if ((current.getType() == TokenType.SEPERATOR)
					|| (current.getType() == TokenType.CLOSE_LIST)) {
				tokenizer.nextToken(); // consume!
				if (current.getType() == TokenType.CLOSE_LIST) {
					return rv;
				}
			} else {
				throw new GenericParseException(
						String.format("Odd, this (token of type %s, value %s) should not be here!",
								current.getType(), current.getValue()));
			}
			current = tokenizer.peek();
			next = tokenizer.peekToken();
		}

		throw new GenericParseException("End of input found while processing a LIST!");
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(LIST, TokenType.IDENTIFIER, TokenType.OPEN_PARENS,
				OPERATION);
	}
}
