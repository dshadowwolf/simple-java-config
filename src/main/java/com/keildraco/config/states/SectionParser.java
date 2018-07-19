package com.keildraco.config.states;

import java.io.StreamTokenizer;
import java.util.Locale;

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
		int p;

		ret_error:
		while( (p = this.nextToken(tok)) != TT_EOF ) {
			String ident = "";
			if(!errored()) {
				if(p == TT_WORD) {
					if(tok.sval.matches(identifierPattern)) {
						// valid identifier, store and get the next token
						ident = String.format("%s", tok.sval).toLowerCase(new Locale("en"));
						int n = this.nextToken(tok);
						if(!errored()) {
							switch(tok.sval) {
							case "{":
								this.section.addItem(this.factory.parseTokens("SECTION", this.section, tok));
								break;
							case "=":
								this.section.addItem(this.factory.parseTokens("KEYVALUE", this.section, tok));
								break;
							case "}":
								return this.section;
							default:
								String mess = String.format("Bad token of type %s (%s) found at line %d",
										ttypeToString(n), tok.sval, tok.lineno());
								System.err.print(mess);
								this.setErrored();
							}
						}
						if(errored()) break ret_error;
					}
				}			
			} else {
				System.err.println("Error parsing at line "+tok.lineno());
				this.setErrored();
				break ret_error;
			}
		}
		return EmptyType;
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