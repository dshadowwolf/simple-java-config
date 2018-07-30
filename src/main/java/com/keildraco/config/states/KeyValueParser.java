package com.keildraco.config.states;

import static java.io.StreamTokenizer.TT_WORD;

import java.io.StreamTokenizer;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.*;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

public class KeyValueParser implements IStateParser {
	private boolean errored = false;
	private String name;
	private ParserInternalTypeBase parent = null;
	private TypeFactory factory;

	public KeyValueParser(TypeFactory factory, String name) {
		this.name = name;
		this.factory = factory;
	}

	public KeyValueParser(TypeFactory factory) {
		this.factory = factory;
		this.name = "Well I'll Be Buggered";
	}
	
	@Override
	public void setErrored() {
		this.errored = true;
	}
	
	@Override
	public boolean errored() {
		return this.errored;
	}

	@Override
	public ParserInternalTypeBase getState(StreamTokenizer tok) {
		int p = this.nextToken(tok);
		
		if(!this.errored() && p == TT_WORD && tok.sval.matches(IDENTIFIER_PATTERN)) {
			String temp = tok.sval;
			if(this.peekToken(tok) == '(') return this.factory.parseTokens("OPERATION", null, tok, temp);
			else return this.factory.getType(this.getParent(), this.name, temp, ItemType.IDENTIFIER);
		} else if(!errored() && p != TT_WORD) {
			switch(p) {
			case StreamTokenizer.TT_EOF:
				Config.LOGGER.error("Premature End of File while parsing a key-value pair, line %d", tok.lineno());
				this.setErrored();
				break;
			case '[':
				return this.factory.parseTokens("LIST", this.parent, tok, this.name);
			case '}':
				tok.pushBack();
				return EmptyType;
			default:
				Config.LOGGER.error("Token of unexpected type %s found where TT_WORD expected, line %d", ttypeToString(p), tok.lineno());
			}
			tok.pushBack();
			return EmptyType;
		} else {
			Config.LOGGER.error("ERROR! ERROR! ERROR! - Error parsing at line "+tok.lineno());
			tok.pushBack();
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
