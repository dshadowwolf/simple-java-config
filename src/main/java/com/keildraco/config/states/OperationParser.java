package com.keildraco.config.states;

import java.io.IOException;
import java.io.StreamTokenizer;

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
			
			while((p = tok.nextToken()) != StreamTokenizer.TT_EOF ) {
				System.err.println(String.format("%s -- %d (%c)", tok.sval, p, p>0&&p<127?p:'?'));
				if(tok.ttype == '(') continue;
				if(operator == null && (tok.ttype == '~' || tok.ttype == '!')) {
					operator = String.format("%c", tok.ttype);
				} else if(operator == null && !(tok.ttype == '~' || tok.ttype == '!')) {
					String mess = String.format("Error parsing an operation - operator not found when expected, got %c instead", tok.ttype);
					System.err.println(mess);
					return ParserInternalTypeBase.EmptyType;
				} else if(operator != null && value == null) {
					if(tok.sval.matches(identifierPattern)) {
						value = tok.sval;
					} else {
						String mess = String.format("Error parsing an operation - expected an identifier and found %s instead", tok.sval);
						System.err.println(mess);
						return ParserInternalTypeBase.EmptyType;
					}
				} else if(operator!=null && value!=null && p == ')') {
					OperationType rv = (OperationType)this.factory.getType(this.getParent(), this.name, value, ItemType.OPERATION);
					rv.setName(this.name);
					rv.setOperation(operator);
					return rv;
				} else {
					String mess = String.format("Error parsing an operation - expected to find a closing parentheses, found %s instead", tok.sval);
					System.err.println(mess);
					return ParserInternalTypeBase.EmptyType;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return ParserInternalTypeBase.EmptyType;
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
