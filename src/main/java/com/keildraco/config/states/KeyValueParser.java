package com.keildraco.config.states;

import javax.annotation.Nullable;

import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;
import static com.keildraco.config.data.Constants.ParserNames.OPERATION;
import static com.keildraco.config.data.Constants.ParserNames.LIST;
import static com.keildraco.config.data.Constants.ParserNames.KEYVALUE;

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
	public KeyValueParser(final TypeFactory factoryIn,
			@Nullable final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, KEYVALUE);
	}

	@Override
	public ParserInternalTypeBase getState(final Tokenizer tokenizer) {
		if (!tokenizer.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		final String key = tokenizer.nextToken().getValue();
		tokenizer.nextToken();

		final Token next = tokenizer.peek();
		final Token following = tokenizer.peekToken();

		if ((next.getType() == TokenType.IDENTIFIER)
				&& ((following.isEmpty()) || (following.getType() != TokenType.OPEN_PARENS))) {
			final ParserInternalTypeBase rv = this.getFactory().getType(null, key, next.getValue(),
					ItemType.IDENTIFIER);
			tokenizer.nextToken();
			return rv;
		}

		final IStateParser parser = this.getFactory().nextState(this.getName(), next, following);
		final ParserInternalTypeBase rv = parser.getState(tokenizer);
		rv.setName(key);
		return rv;
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(KEYVALUE, TokenType.OPEN_LIST, TokenType.IDENTIFIER, LIST);
		factory.registerStateTransition(KEYVALUE, TokenType.OPEN_PARENS, TokenType.NOT,
				OPERATION);
		factory.registerStateTransition(KEYVALUE, TokenType.OPEN_PARENS, TokenType.TILDE,
				OPERATION);
	}
}
