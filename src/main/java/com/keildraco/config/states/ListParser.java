/**
 * 
 */
package com.keildraco.config.states;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Deque;
import java.util.LinkedList;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.*;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

/**
 * @author Daniel Hazelton
 *
 */
public class ListParser implements IStateParser {
	@SuppressWarnings("unused")
	private String name;
	private boolean errored = false;
	private ParserInternalTypeBase parent;
	private TypeFactory factory;
	
	/**
	 * 
	 */
	public ListParser(TypeFactory factory, String name) {
		this.name = name;
		this.factory = factory;
	}

	/* (non-Javadoc)
	 * @see com.keildraco.config.states.IStateParser#setErrored()
	 */
	@Override
	public void setErrored() {
		this.errored = true;
	}

	@Override
	public boolean errored() {
		return this.errored;
	}
	
	/* (non-Javadoc)
	 * @see com.keildraco.config.states.IStateParser#getState(java.io.StreamTokenizer)
	 */
	@Override
	public ParserInternalTypeBase getState(StreamTokenizer tok) {
		int p;
		Deque<ParserInternalTypeBase> store = new LinkedList<>();
		
		while((p = this.nextToken(tok)) != StreamTokenizer.TT_EOF) {
			if(!errored() && p == StreamTokenizer.TT_WORD) {
				if(tok.sval.matches(identifierPattern)) {
					String ident = String.format("%s", tok.sval);
					int n = this.peekToken(tok);
					if(n == StreamTokenizer.TT_WORD) {
						if(tok.sval.equals("(")) {
							store.push(parseOperation(ident,tok));
						} else {
							store.push(this.factory.getType(this.getParent(), this.name, ident, ItemType.IDENTIFIER));
						}
					}
				} else if(tok.sval.matches(numberPattern)) {
					store.push(this.factory.getType(this.getParent(), this.name, tok.sval, ItemType.NUMBER));
				} else if(tok.sval.toLowerCase().matches("\\s*(?:true|false)\\s*")) {
					store.push(this.factory.getType(this.getParent(), this.name, tok.sval, ItemType.BOOLEAN));
				} else {
					System.err.println("Error parsing at line "+tok.lineno());
				}
			}
		}
		return null;
	}

	/**
	 * Parse an "operation" from the token stream
	 * This gets called when the opening parentheses of one is found and will return the
	 * "OperationType" of the final operation as parsed.
	 * 
	 * @param ident Overall name for the operation, as parsed from the input data - an Identifier
	 * @param tok StreamTokenizer to use for input
	 * @return a value of type OperationType containing the parsed data of the operation or ParserInternalTypeBase.EmptyType on error
	 */
	private ParserInternalTypeBase parseOperation(String ident, StreamTokenizer tok) {
		try {
			String operator = null;
			String value = null;
			
			while(tok.nextToken() == StreamTokenizer.TT_WORD && !tok.sval.equals(")")) {
				if(operator == null && tok.sval.matches("\\s*[~!]\\s*")) {
					operator = tok.sval;
				} else if(operator == null && !tok.sval.matches("\\s*[~!]\\s*")) {
					String mess = String.format("Error parsing an operation - operator not found when expected, got %s instead", tok.sval);
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
				} else {
					String mess = String.format("Error parsing an operation - expected to find a closing parentheses, found %s instead", tok.sval);
					System.err.println(mess);
					return ParserInternalTypeBase.EmptyType;
				}
			}
			if(operator!=null && value!=null) {
				OperationType rv = (OperationType)this.factory.getType(this.getParent(), this.name, value, ItemType.OPERATION);
				return rv.setOperation(operator);
			} else {
				String mess = String.format("Error parsing an operation - found a token of type %s instead of %s", 
						ttypeToString(tok.ttype), ttypeToString(StreamTokenizer.TT_WORD));
				System.err.println(mess);
				return ParserInternalTypeBase.EmptyType;
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
	public void setFactory(TypeFactory factory) {
		this.factory = factory;
	}

	@Override
	public TypeFactory getFactory() {
		return this.factory;
	}
}