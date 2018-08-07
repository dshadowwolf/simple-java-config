package com.keildraco.config.states;

import javax.annotation.Nullable;

import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.OperationType;
import static com.keildraco.config.data.Constants.ParserNames.OPERATION;

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
		super(factoryIn, parentIn, OPERATION);
	}

	@Override
	public ParserInternalTypeBase getState(final Tokenizer tokenizer) {
		if (!tokenizer.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		final String key = tokenizer.nextToken().getValue();

		tokenizer.nextToken();
		final Token operT = tokenizer.nextToken();
		final Token value = tokenizer.nextToken();
		final Token last = tokenizer.nextToken();

		if ((operT.getType() != TokenType.NOT) && (operT.getType() != TokenType.TILDE)) {
			if (operT.getType() == TokenType.IDENTIFIER) {
				throw new GenericParseException(
						"Found an Identifier where an Operator was expected");
			} else {
				throw new GenericParseException(
						String.format("Found %s where an Operator was expected", operT.getValue()));
			}
		}

		if (value.getType() != TokenType.IDENTIFIER) {
			throw new GenericParseException(
					String.format("Found %s where an Identifier was expected", operT.getValue()));
		}

		if (last.getType() == TokenType.CLOSE_PARENS) {
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
