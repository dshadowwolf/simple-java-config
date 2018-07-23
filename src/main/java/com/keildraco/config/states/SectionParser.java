package com.keildraco.config.states;

import java.io.StreamTokenizer;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.SectionType;
import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

import static java.io.StreamTokenizer.*;

public class SectionParser implements IStateParser {
	private boolean errored = false;
	private String name;
	private SectionType section;
	private ParserInternalTypeBase parent;
	private TypeFactory factory;
	
	public SectionParser(TypeFactory factory) {
		this.name = "ROOT";
		this.section = new SectionType(EmptyType, this.name);
		this.factory = factory;
	}

	public SectionParser(TypeFactory factory, SectionType parent, String name) {
		this.name = name;
		this.parent = parent;
		this.factory = factory;
		this.section = new SectionType(parent, this.name);
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
		String ident = "";
		while( this.nextToken(tok) != TT_EOF && !this.errored()) {
			int tt = getTokenType(tok);
			switch(tt) {
			case '=':
				if(ident.equals("")) {
					this.setErrored();
					System.err.println("Found a store operation (=) where I was expecting an identifier");
					return EmptyType;
				}
				this.section.addItem(this.factory.parseTokens("KEYVALUE", this.section, tok, ident));
				ident = "";
				break;
			case '{':
				ParserInternalTypeBase gg = this.factory.parseTokens("SECTION", this.section, tok, ident.equals("")?this.name:ident);
				gg.setName(ident.equals("")?this.name:ident);
				this.section.addItem(gg);
				ident = "";
				break;
			case '}':
				return this.section;
			case -1:
				ident = tok.sval.trim();
				break;
			case -2:
			case -3:
			case -4:
			default:
				ident = "";
				this.setErrored();
				System.err.println(String.format("Found %s where it was not expected - this is an error", itToString(tt)));
				return EmptyType;
			}
		}
		return this.section;
	}

	private String itToString(int tt) {
		switch(tt) {
		case -1:
			return "an Identifier";
		default:
			return String.format("'%c'", tt);
		}
	}

	private static int getTokenType(StreamTokenizer tok) {
		if(tok.ttype == TT_WORD) {
			if(tok.sval.matches(identifierPattern)) return -1;
			else return -4;
		} else {
			return tok.ttype;
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
