package com.keildraco.config.states;

import java.io.IOException;
import java.io.StreamTokenizer;
import static java.io.StreamTokenizer.*;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.ParserInternalTypeBase;

public interface IStateParser {
	public static final String IDENTIFIER_PATTERN = "^\\s*[a-zA-Z_]{1}[a-zA-Z0-9_]*\\s*$";
	
	public void setFactory(TypeFactory factory);
	public TypeFactory getFactory();
	
	default public String ttypeToString(final int ttype) {
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
	public boolean errored();
	
	default public int peekToken(final StreamTokenizer tok) {
		int k = StreamTokenizer.TT_EOF;
		try {
			k = tok.nextToken();
		} catch (final IOException e) {
			this.setErrored();
			Config.LOGGER.error(e.getStackTrace());
		} finally {
			tok.pushBack();
		}
		return k;
	}
	
	default public int nextToken(final StreamTokenizer tok) {
		int k = StreamTokenizer.TT_EOF;
		try {
			k = tok.nextToken();
		} catch (final IOException e) {
			this.setErrored();
			Config.LOGGER.error(e.getStackTrace());
		}
		return k;
	}
	
	public ParserInternalTypeBase getState(final StreamTokenizer tok);
	public void setParent(final ParserInternalTypeBase parent);
	public ParserInternalTypeBase getParent();
	default public void setName(final String name) { /* this space intentionally blank */ }
	public String getName();
	public void clearErrors();
}
