package com.keildraco.config.states;

import javax.annotation.Nullable;

import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.OperationType;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class OperationParser extends AbstractParserBase {

	/**
	 *
	 * @param factoryIn
	 * @param parentIn
	 */
	public OperationParser(final TypeFactory factoryIn,
			@Nullable final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "OPERATION");
	}

	@Override
	public ParserInternalTypeBase getState(final Tokenizer tok) {
		if (!tok.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		final String key = tok.nextToken().getValue();

		tok.nextToken();
		final Token operT = tok.nextToken();
		final Token value = tok.nextToken();
		final Token last = tok.nextToken();
		switch (operT.getType()) {
			case NOT:
			case TILDE:
				break;
			case IDENTIFIER:
				throw new GenericParseException(
						"Found an Identifier where an Operator was expected");
			default:
				throw new GenericParseException(
						String.format("Found %s where an Operator was expected", operT.getValue()));
		}

		if (!value.getType().equals(TokenType.IDENTIFIER)) {
			throw new GenericParseException(
					String.format("Found %s where an Identifier was expected", operT.getValue()));
		}

		if (last.getType().equals(TokenType.CLOSE_PARENS)) {
			final OperationType rv = (OperationType) this.getFactory().getType(null, key,
					value.getValue(), ItemType.OPERATION);
			rv.setOperation(operT.getValue());
			return rv;
		}

		throw new GenericParseException(
				"Found " + last.getValue() + " where a closing parenthesis was expected");
	}

	@Override
	public void registerTransitions(@Nullable final TypeFactory factory) {
		// blank - no transitions here
	}
}
