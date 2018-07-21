package com.keildraco.config.states;

import static java.io.StreamTokenizer.TT_WORD;

import java.io.StreamTokenizer;

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
		
		if(!this.errored() && p == TT_WORD ) {
			switch(tok.sval.toLowerCase()) {
			case "true":
			case "false":
				System.err.println("Boolean: "+tok.sval);
				return this.factory.getType(this.getParent(), this.name, tok.sval, ItemType.BOOLEAN);
			default:
				if(tok.sval.matches(identifierPattern)) {
					System.err.println("Identifier: "+tok.sval);
					return this.factory.getType(this.getParent(), this.name, tok.sval, ItemType.IDENTIFIER);
				} else if(tok.sval.matches(numberPattern)) {
					System.err.println("Number: "+tok.sval);
					return this.factory.getType(this.getParent(), this.name, tok.sval, ItemType.NUMBER);
				} else {
					String mess = String.format("Unknown item of type TT_WORD (%s) on line %d", tok.sval, tok.lineno());
					System.err.println(mess);
					return EmptyType;
				}
			}
		} else if(!errored() && p != TT_WORD) {
			switch(p) {
			case StreamTokenizer.TT_EOF:
				System.err.println(String.format("Premature End of File while parsing a key-value pair, line %d", tok.lineno()));
				break;
			case '[':
				System.err.println("Starting List Parsing:");
				return this.factory.parseTokens("LIST", this.parent, tok, this.name);
			case '}':
				tok.pushBack();
				return EmptyType;
			default:
				System.err.println(String.format("Token of unexpected type %s found where TT_WORD expected, line %d", ttypeToString(p), tok.lineno()));
			}
			tok.pushBack();
			return EmptyType;
		} else {
			System.err.println("ERROR! ERROR! ERROR! - Error parsing at line "+tok.lineno());
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
