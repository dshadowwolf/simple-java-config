package com.keildraco.config.tokenizer;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Deque;
import java.util.LinkedList;

import com.keildraco.config.data.Token;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class Tokenizer {

	/**
	 *
	 */
	private final Deque<Token> tokens;

	/**
	 *
	 * @param tok
	 * @throws IOException
	 */
	public Tokenizer(final StreamTokenizer tok) throws IOException {
		this.tokens = new LinkedList<>();
		
		// setup the StreamTokenizer exactly how we want it
		tok.resetSyntax();
		tok.whitespaceChars(0, ' ');
		tok.slashSlashComments(true);
		tok.slashStarComments(true);
		tok.commentChar('#');
		tok.wordChars('a', 'z');
		tok.wordChars('A', 'Z');
		tok.wordChars('0', '9');
		tok.wordChars('_', '_');
		tok.wordChars('-', '-');

		int p;
		while ((p = tok.nextToken()) != StreamTokenizer.TT_EOF) {
			if (p == StreamTokenizer.TT_WORD) {
				this.tokens.addLast(new Token(tok.sval));
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
		if (this.tokens.isEmpty()) {
			return null;
		}

		final Token k = this.tokens.pop();
		final Token rv = this.tokens.peek();
		this.tokens.push(k);
		return rv;
	}

	/**
	 *
	 * @return
	 */
	public Token peek() {
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
