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

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.*;
import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;
import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

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
			if(tok.sval.matches(IDENTIFIER_PATTERN)) {
				ident = tok.sval;
				if(!errored() && p == StreamTokenizer.TT_WORD) {
					ParserInternalTypeBase temp = this.getToken(tok, ident);
					temp.setName(ident);
					store.push(temp);
				}
			}
		}
		
		List<ParserInternalTypeBase> l = store.stream().collect(Collectors.toList());
		Collections.reverse(l);
		return new ListType(this.name, l);
	}

	private ParserInternalTypeBase getToken(StreamTokenizer tok, String ident) {
		if(tok.sval.matches(IDENTIFIER_PATTERN)) {
			int n = this.peekToken(tok);
			if(n == StreamTokenizer.TT_WORD || n == ',' || n == ']') {
				return this.factory.getType(this.getParent(), this.getName(), ident, ItemType.IDENTIFIER);
			} else if( n == '(') {
				return this.factory.parseTokens("OPERATION", this.getParent(), tok, ident);
			}
		} else {
			Config.LOGGER.error("Error parsing at line "+tok.lineno());
			this.setErrored();
			return EmptyType;
		}
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
