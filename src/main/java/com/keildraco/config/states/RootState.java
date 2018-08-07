package com.keildraco.config.states;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;

import javax.annotation.Nullable;

import com.keildraco.config.Config;
import com.keildraco.config.data.BasicResult;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class RootState extends AbstractParserBase {

	/**
	 *
	 * @param factoryIn
	 * @param parentIn
	 */
	public RootState(final TypeFactory factoryIn, @Nullable final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "ROOT");
	}

	/**
	 *
	 * @param tokenizer
	 * @return
	 */
	@Override
	public ParserInternalTypeBase getState(final Tokenizer tokenizer) {
		if (!tokenizer.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		Token current = tokenizer.peek();
		Token next = tokenizer.peekToken();

		final Deque<ParserInternalTypeBase> bits = new LinkedList<>();

		while (tokenizer.hasNext()) {
			try {
				bits.push(this.getFactory().nextState(this.getName(), current, next)
						.getState(tokenizer));
			} catch (UnknownStateException e) {
				Config.LOGGER.error("Exception during parse: %s", e.getMessage());
				Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
				return ParserInternalTypeBase.EMPTY_TYPE;
			}
			current = tokenizer.peek();
			next = tokenizer.peekToken();
		}

		final ParserInternalTypeBase rv = new BasicResult(this.getName());

		bits.forEach(rv::addItem);
		return rv;
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(this.getName(), TokenType.IDENTIFIER, TokenType.OPEN_BRACE,
				"SECTION");
		factory.registerStateTransition(this.getName(), TokenType.IDENTIFIER, TokenType.STORE,
				"KEYVALUE");
	}
}
