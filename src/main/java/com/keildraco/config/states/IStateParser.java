package com.keildraco.config.states;

import java.io.IOException;
import java.io.StreamTokenizer;
import static java.io.StreamTokenizer.*;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.ParserInternalTypeBase;

public interface IStateParser {
	public static final String identifierPattern = "^\\s*[a-zA-Z0-9_][a-zA-Z0-9_\\-]*\\s*$";
	public static final String operatorPattern = "^\\s*[a-zA-Z0-9_][a-zA-Z0-9_\\-]*\\(\\s*[!~]\\s*[a-zA-Z0-9_][a-zA-Z0-9_\\\\-]*\\s*\\)\\s*$";
	
	public void setFactory(TypeFactory factory);
	public TypeFactory getFactory();
	
	default public String ttypeToString(int ttype) {
		switch(ttype) {
		case TT_WORD:
			return "TT_WORD";
		case TT_NUMBER:
			return "TT_NUMBER";
		case TT_EOL:
			return "TT_EOL";
		case TT_EOF:
			return "TT_EOF";
		default:
			return "UNKNOWN";
		}
	}
	
	public void setErrored();
	default public boolean errored() { return false; }
	
	default public int peekToken(StreamTokenizer tok) {
		int k = StreamTokenizer.TT_EOF;
		try {
			k = tok.nextToken();
		} catch (IOException e) {
			this.setErrored();
			e.printStackTrace();
		} finally {
			tok.pushBack();
		}
		return k;
	}
	
	default public int nextToken(StreamTokenizer tok) {
		int k = StreamTokenizer.TT_EOF;
		try {
			k = tok.nextToken();
		} catch (IOException e) {
			this.setErrored();
			e.printStackTrace();
		}
		return k;
	}
	
	public ParserInternalTypeBase getState(StreamTokenizer tok);
	public void setParent(ParserInternalTypeBase parent);
	public ParserInternalTypeBase getParent();
	default public void setName(String name) { return; }
	public String getName();
	public void clearErrors();
}
