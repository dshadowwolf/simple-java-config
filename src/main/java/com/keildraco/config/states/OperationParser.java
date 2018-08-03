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
	public OperationParser(final TypeFactory factoryIn, final ParserInternalTypeBase parentIn) {
		super(factoryIn, parentIn, "OPERATION");
	}

	@Override
	public ParserInternalTypeBase getState(final Tokenizer tok)
			throws IllegalParserStateException, UnknownStateException, GenericParseException {
		if (!tok.hasNext()) {
			throw new IllegalParserStateException("End of input at start of state");
		}

		String key = tok.nextToken().getValue();
		String oper;

		tok.nextToken();
		Token operT = tok.nextToken();
		switch (operT.getType()) {
			case NOT:
			case TILDE:
				oper = operT.getValue();
				break;
			case IDENTIFIER:
				throw new GenericParseException(
						"Found an Identifier where an Operator was expected");
			default:
				throw new GenericParseException(
						String.format("Found %s where an Operator was expected", operT.getValue()));
		}

		operT = tok.nextToken();
		if (!operT.getType().equals(TokenType.IDENTIFIER)) {
			throw new GenericParseException(
					String.format("Found %s where an Identifier was expected", operT.getValue()));
		}

		String value = operT.getValue();
		operT = tok.nextToken();
		if (operT.getType().equals(TokenType.CLOSE_PARENS)) {
			OperationType rv = (OperationType) this.getFactory().getType(null, key, value,
					ItemType.OPERATION);
			rv.setOperation(oper);
			return rv;
		}

		throw new GenericParseException(
				"Found " + operT.getValue() + " where a closing parenthesis was expected");
	}

	@Override
	public void registerTransitions(final TypeFactory factory) {
		// blank - no transitions here
	}
}
