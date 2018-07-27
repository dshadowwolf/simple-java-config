/**
 * 
 */
package com.keildraco.config.states;

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
	
	public ListParser(TypeFactory factory) {
		this.factory = factory;
		this.name = "Well I'll Be Buggered";
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
		String ident;
		while((p = this.nextToken(tok)) != StreamTokenizer.TT_EOF && p != ']') {
			if(p=='[') continue;
 			if(!errored() && p == StreamTokenizer.TT_WORD) {
				if(tok.sval.matches(identifierPattern)) {
					ident = tok.sval;
					int n = this.peekToken(tok);
					if(n == StreamTokenizer.TT_WORD || n == ',' || n == ']') {
						ParserInternalTypeBase zzz = this.factory.getType(this.getParent(), this.name, ident, ItemType.IDENTIFIER);
						store.push(zzz);
					} else if( n == '(') {
						ParserInternalTypeBase temp = this.factory.getType(this.getParent(), this.name, ident, ItemType.OPERATION);
						store.push(temp);
					}
				} else {
					System.err.println("Error parsing at line "+tok.lineno());
				}
			}
//			store.stream().forEach(it -> System.err.println(it.asString()));
		}
		List<ParserInternalTypeBase> l = store.stream().collect(Collectors.toList());
		Collections.reverse(l);
		return new ListType(this.name, l);
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
