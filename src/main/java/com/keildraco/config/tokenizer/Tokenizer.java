package com.keildraco.config.tokenizer;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Deque;
import java.util.LinkedList;

import com.keildraco.config.data.Constants;
import com.keildraco.config.data.Token;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class Tokenizer {

	/**
	 * Internal storage of the token stream derived from the StreamTokenizer that was a parameter of
	 * the constructor
	 */
	private final Deque<Token> tokens;

	/**
	 * <p>
	 * Constructor (seriously, the only one for this class)
	 * </p>
	 * <p>
	 * Takes a {@link java.io.StreamTokenizer StreamTokenizer} and iterates its results, turning it
	 * into a uniform stream of {@link com.keildraco.config.data.Token Tokens} that get later used
	 * by the parser.
	 * </p>
	 * 
	 * @param tokenizer
	 *            A {@link java.io.StreamTokenizer StreamTokenizer} instance that is used as the raw
	 *            lexical source for the uninterpreted tokens
	 * @throws IOException
	 *             Same reasons as {@link java.io.StreamTokenizer StreamTokenizer} would, since that
	 *             is the source of this exception, in this case.
	 */
	public Tokenizer(final StreamTokenizer tokenizer) throws IOException {
		this.tokens = new LinkedList<>();

		// setup the StreamTokenizer exactly how we want it
		tokenizer.resetSyntax();
		tokenizer.whitespaceChars(0, ' ');
		tokenizer.slashSlashComments(true);
		tokenizer.slashStarComments(true);
		tokenizer.commentChar('#');
		tokenizer.wordChars('a', 'z');
		tokenizer.wordChars('A', 'Z');
		tokenizer.wordChars('0', '9');
		tokenizer.wordChars('_', '_');
		tokenizer.wordChars('-', '-');

		int p;
		while ((p = tokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
			if (p == StreamTokenizer.TT_WORD) {
				this.tokens.addLast(new Token(tokenizer.sval));
			} else {
				this.tokens.addLast(new Token(String.format("%C", p)));
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public Token nextToken() {
		if (this.tokens.isEmpty()) {
			return new Token(Constants.TOKENEMPTY);
		}
		return this.tokens.pop();
	}

	/**
	 *
	 * @return
	 */
	public boolean hasNext() {
		return !this.tokens.isEmpty();
	}

	/**
	 *
	 * @return
	 */
	public Token peekToken() {
		if (this.tokens.isEmpty() || this.tokens.size() == 1) {
			return new Token(Constants.TOKENEMPTY);
		}

		final Token tok = this.tokens.pop();
		final Token rv = this.tokens.peek();
		this.tokens.push(tok);
		return rv;
	}

	/**
	 *
	 * @return
	 */
	public Token peek() {
		if (this.tokens.isEmpty()) {
			return new Token(Constants.TOKENEMPTY);
		}

		return this.tokens.peek();
	}

	/**
	 *
	 * @param token
	 */
	public void pushBack(final Token token) {
		this.tokens.push(token);
	}
}
