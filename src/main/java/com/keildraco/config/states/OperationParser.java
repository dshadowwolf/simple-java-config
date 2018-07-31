package com.keildraco.config.states;

import java.io.IOException;
import java.io.StreamTokenizer;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

public class OperationParser implements IStateParser {
	private TypeFactory factory;
	private final String name;
	private ParserInternalTypeBase parent;
	private boolean error = false;

	public OperationParser(final TypeFactory factory) {
		this.factory = factory;
		this.name = "Well I'll Be Buggered";
	}

	public OperationParser(final TypeFactory factory, final ParserInternalTypeBase parent, final String name) {
		this.factory = factory;
		this.name = name;
		this.parent = parent;
	}

	@Override
	public void setFactory(final TypeFactory factory) {
		this.factory = factory;
	}

	@Override
	public TypeFactory getFactory() {
		return this.factory;
	}

	@Override
	public void setErrored() {
		this.error = true;
	}

	@Override
	public boolean errored() {
		return this.error;
	}

	@Override
	public ParserInternalTypeBase getState(final StreamTokenizer tok) {
		try {
			tok.nextToken();
			if (tok.ttype == '(') tok.nextToken();

			if (tok.ttype != StreamTokenizer.TT_EOF) {
				final String operator = this.getOperator(tok);
				final String value = this.getIdentifier(tok);
				final int p = peekToken(tok);
				if (p == ')') {
					final OperationType rv = (OperationType) this.factory.getType(this.getParent(), this.name, value, ItemType.OPERATION);
					rv.setName(this.name);
					rv.setOperation(operator);
					return rv;
				} else {
					Config.LOGGER.error("Error parsing an operation - expected to find a closing parentheses, found %s instead", tok.sval);
					return ParserInternalTypeBase.EmptyType;
				}
			}
		} catch (final IOException | IllegalArgumentException e) {
			Config.LOGGER.error("Exception parsing Operation: %s", e.getMessage());
			Config.LOGGER.error(e.getStackTrace());
			this.setErrored();
			return ParserInternalTypeBase.EmptyType;
		}
		return ParserInternalTypeBase.EmptyType;
	}

	private String getIdentifier(final StreamTokenizer tok) {
		this.nextToken(tok);
		if (tok.ttype == StreamTokenizer.TT_WORD && tok.sval.matches(IDENTIFIER_PATTERN)) return tok.sval;
		throw new IllegalArgumentException("IDENTIFIER not available in token stream");
	}

	private String getOperator(final StreamTokenizer tok) {
		if (tok.ttype=='~' || tok.ttype=='!') return String.format("%c", tok.ttype);
		throw new IllegalArgumentException("OPERATOR not available in token stream");
	}

	@Override
	public void setParent(final ParserInternalTypeBase parent) {
		this.parent = parent;
	}

	@Override
	public ParserInternalTypeBase getParent() {
		return this.parent;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void clearErrors() {
		this.error = false;
	}
}
