package com.keildraco.config.states;

import static java.io.StreamTokenizer.TT_EOF;
import static java.io.StreamTokenizer.TT_EOL;
import static java.io.StreamTokenizer.TT_NUMBER;
import static java.io.StreamTokenizer.TT_WORD;

import java.io.IOException;
import java.io.StreamTokenizer;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.ParserInternalTypeBase;

public interface IStateParser {

	String IDENTIFIER_PATTERN = "^\\s*[a-zA-Z_]{1}[a-zA-Z0-9_]*\\s*$";

	void setFactory(TypeFactory factory);

	TypeFactory getFactory();

	/**
	 *
	 * @param ttype
	 * @return
	 */
	default String ttypeToString(final int ttype) {
		switch (ttype) {
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

	void setErrored();

	boolean errored();

	/**
	 *
	 * @param tok
	 * @return
	 */
	default int peekToken(final StreamTokenizer tok) {
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

	/**
	 *
	 * @param tok
	 * @return
	 */
	default int nextToken(final StreamTokenizer tok) {
		int k = StreamTokenizer.TT_EOF;
		try {
			k = tok.nextToken();
		} catch (final IOException e) {
			this.setErrored();
			Config.LOGGER.error(e.getStackTrace());
		}
		return k;
	}

	ParserInternalTypeBase getState(StreamTokenizer tok);

	void setParent(ParserInternalTypeBase parent);

	ParserInternalTypeBase getParent();

	default void setName(final String name) {
		/* this space intentionally blank */
	}

	String getName();

	void clearErrors();
}
