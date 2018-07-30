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
	private String name;
	private ParserInternalTypeBase parent;
	private boolean error = false;
	
	public OperationParser(TypeFactory factory) {
		this.factory = factory;
		this.name = "Well I'll Be Buggered";
	}
	
	public OperationParser(TypeFactory factory, ParserInternalTypeBase parent, String name) {
		this.factory = factory;
		this.name = name;
		this.parent = parent;
	}
	
	@Override
	public void setFactory(TypeFactory factory) {
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
	public ParserInternalTypeBase getState(StreamTokenizer tok) {
		try {
			String operator = null;
			String value = null;
			int p;

			tok.nextToken();
			if(tok.ttype == '(') tok.nextToken();

			if(tok.ttype != StreamTokenizer.TT_EOF) {
				operator = this.getOperator(tok);
				value = this.getIdentifier(tok);
				p = peekToken(tok);
				if(p == ')') {
					OperationType rv = (OperationType)this.factory.getType(this.getParent(), this.name, value, ItemType.OPERATION);
					rv.setName(this.name);
					rv.setOperation(operator);
					return rv;					
				} else {
					Config.LOGGER.error("Error parsing an operation - expected to find a closing parentheses, found %s instead", tok.sval);
					return ParserInternalTypeBase.EmptyType;
				}
			}
		} catch(IOException | IllegalArgumentException e) {
			Config.LOGGER.error("Exception parsing Operation: %s", e.getMessage());
			Config.LOGGER.error(e.getStackTrace());
			this.setErrored();
			return ParserInternalTypeBase.EmptyType;
		}
		return ParserInternalTypeBase.EmptyType;
	}
	
	private String getIdentifier(StreamTokenizer tok) {
		this.nextToken(tok);
		if(tok.ttype == StreamTokenizer.TT_WORD && tok.sval.matches(IDENTIFIER_PATTERN)) return tok.sval;
		throw new IllegalArgumentException("IDENTIFIER not available in token stream");
	}

	private String getOperator(StreamTokenizer tok) {
		if(tok.ttype=='~'||tok.ttype=='!') return String.format("%c", tok.ttype);
		throw new IllegalArgumentException("OPERATOR not available in token stream");
	}

	@Override
	public void setParent(ParserInternalTypeBase parent) {
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
