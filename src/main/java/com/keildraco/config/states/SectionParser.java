package com.keildraco.config.states;

import javax.annotation.Nullable;

import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.SectionType;
import static com.keildraco.config.data.Constants.ParserNames.SECTION;
import static com.keildraco.config.data.Constants.ParserNames.KEYVALUE;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class SectionParser extends AbstractParserBase {

	/**
	 *
	 * @param factoryIn
	 * @param parentIn
	 */
	public SectionParser(final TypeFactory factoryIn,
			@Nullable final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, SECTION);
	}

	@Override
	public ParserInternalTypeBase getState(final Tokenizer tokenizer) {
		if (!tokenizer.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		final String sectionName = tokenizer.nextToken().getValue();
		tokenizer.nextToken(); // skip the OPEN_BRACE

		Token current = tokenizer.peek();
		Token next = tokenizer.peekToken();

		final SectionType rv = new SectionType(sectionName);

		while (tokenizer.hasNext()) {
			if (current.getType() == TokenType.CLOSE_BRACE) {
				tokenizer.nextToken();
				rv.setName(sectionName); // force this, despite what other code thinks
				return rv;
			}

			com.keildraco.config.Config.LOGGER.fatal("(A) name: %s -- current: %s -- next: %s", this.getName(), current.getValue(), next.getValue());
			
			rv.addItem(
					this.getFactory().nextState(this.getName(), current, next).getState(tokenizer));
			current = tokenizer.peek();
			next = tokenizer.peekToken();
			com.keildraco.config.Config.LOGGER.fatal("(B) name: %s -- current: %s -- next: %s", this.getName(), current.getValue(), next.getValue());
		}

		throw new GenericParseException("End of input while parsing a SECTION");
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		factory.registerStateTransition(SECTION, TokenType.IDENTIFIER, TokenType.STORE, KEYVALUE);
		factory.registerStateTransition(SECTION, TokenType.IDENTIFIER, TokenType.OPEN_BRACE,
				SECTION);
	}
}
