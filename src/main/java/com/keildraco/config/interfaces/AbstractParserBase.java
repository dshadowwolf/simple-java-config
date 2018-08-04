package com.keildraco.config.interfaces;

import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.Locale;

import javax.annotation.Nullable;

import com.keildraco.config.Config;
import com.keildraco.config.data.BasicResult;
import com.keildraco.config.data.Token;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
public abstract class AbstractParserBase implements IStateParser {

	/**
	 *
	 */
	private TypeFactory factory;

	/**
	 *
	 */
	private ParserInternalTypeBase parent;

	/**
	 *
	 */
	private String name;

	/**
	 *
	 * @param factoryIn
	 * @param parentIn
	 * @param nameIn
	 */
	public AbstractParserBase(@Nullable final TypeFactory factoryIn,
			@Nullable final ParserInternalTypeBase parentIn, final String nameIn) {
		this.factory = factoryIn;
		this.parent = parentIn;
		this.name = nameIn;
	}

	/**
	 *
	 */
	@Override
	public void setFactory(final TypeFactory factoryIn) {
		this.factory = factoryIn;
	}

	/**
	 *
	 */
	@Override
	public TypeFactory getFactory() {
		return this.factory;
	}

	/**
	 *
	 */
	@Override
	public void setParent(final ParserInternalTypeBase parentIn) {
		this.parent = parentIn;
	}

	/**
	 *
	 */
	@Override
	public ParserInternalTypeBase getParent() {
		return this.parent;
	}

	/**
	 *
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 *
	 */
	@Override
	public void setName(final String nameIn) {
		this.name = nameIn;
	}

	/**
	 *
	 */
	@Override
	public ParserInternalTypeBase getState(final Tokenizer tok)
			throws IllegalParserStateException, UnknownStateException, GenericParseException {
		if (!tok.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		Token current = tok.peek();
		Token next = tok.peekToken();

		final Deque<ParserInternalTypeBase> bits = new LinkedList<>();

		while (tok.hasNext()) {
			try {
				bits.push(
						this.factory.nextState(this.name.toUpperCase(Locale.ENGLISH), current, next)
								.getState(tok));
			} catch (UnknownStateException e) {
				Config.LOGGER.error("Exception during parse: %s", e.getMessage());
				Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
				return ParserInternalTypeBase.EMPTY_TYPE;
			}
			current = tok.peek();
			next = tok.peekToken();
		}

		final ParserInternalTypeBase rv = new BasicResult(this.name.toUpperCase(Locale.ENGLISH));

		bits.stream().forEach(rv::addItem);
		return rv;
	}
}
