package com.keildraco.config.states;

import static java.io.StreamTokenizer.TT_WORD;

import java.io.StreamTokenizer;

import com.keildraco.config.types.*;
import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

public class KeyValueParser implements IStateParser {
	private boolean errored = false;
	private String name;
	private ParserInternalTypeBase parent;
	
	public KeyValueParser(String name) {
		this.name = name;
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
		String ident = "";

		if(!errored() && p == TT_WORD) {
			switch(tok.sval.toLowerCase()) {
			case "[":
				return new ListParser(this.name).getState(tok);
			case "true":
			case "false":
				return new BooleanType(this.name, Boolean.parseBoolean(tok.sval));
			default:
				if(tok.sval.matches(identifierPattern)) {
					return new IdentifierType(this.name, tok.sval);
				} else if(tok.sval.matches(numberPattern)) {
					return new NumberType(this.name, tok.sval);
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
}
