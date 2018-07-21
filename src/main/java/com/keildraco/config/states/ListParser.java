/**
 * 
 */
package com.keildraco.config.states;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

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
		
		while((p = this.nextToken(tok)) != StreamTokenizer.TT_EOF && p != ']') {
			if(p=='[') continue;
			System.err.print(tok.sval+" ");
			System.err.print(String.format("[%d] ", p));
			if( p < 127 && p >= 32) System.err.println(String.format("(%c)", p));
			else System.err.println("("+ttypeToString(p)+")");
 			if(!errored() && p == StreamTokenizer.TT_WORD || p == '(') {
				if(tok.sval.matches(identifierPattern)) {
					String ident = tok.sval;
					int n = this.peekToken(tok);
					if(n == StreamTokenizer.TT_WORD || n == ',' || n == ']') {
						System.err.println("Identifier: "+ident);
						ParserInternalTypeBase zzz = this.factory.getType(this.getParent(), this.name, ident, ItemType.IDENTIFIER);
						store.push(zzz);
					} else if( n == '(') {
						ParserInternalTypeBase temp = this.parseOperation(ident, tok);
						System.err.println("Operation: "+temp.asString());
						store.push(temp);
					}
				} else if(tok.sval.matches(numberPattern)) {
					System.err.println("Number: "+tok.sval);
					store.push(this.factory.getType(this.getParent(), this.name, tok.sval, ItemType.NUMBER));
				} else if(tok.sval.toLowerCase().matches("\\s*(?:true|false)\\s*")) {
					System.err.println("Boolean: "+tok.sval);
					store.push(this.factory.getType(this.getParent(), this.name, tok.sval, ItemType.BOOLEAN));
				} else {
					System.err.println("Error parsing at line "+tok.lineno());
				}
			}
			store.stream().forEach(it -> System.err.println(it.asString()));
		}
		List<ParserInternalTypeBase> l = store.stream().collect(Collectors.toList());
		Collections.reverse(l);
		return new ListType(this.name, l);
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
			int p;
			
			while((p = tok.nextToken()) != StreamTokenizer.TT_EOF ) {
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
					rv.setName(ident);
					return rv.setOperation(operator);
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
	public void setFactory(TypeFactory factory) {
		this.factory = factory;
	}

	@Override
	public TypeFactory getFactory() {
		return this.factory;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void clearErrors() {
		this.errored = false;
	}
}
