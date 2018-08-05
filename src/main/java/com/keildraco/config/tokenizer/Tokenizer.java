package com.keildraco.config.tokenizer;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Deque;
import java.util.LinkedList;

import com.keildraco.config.data.Token;

import javax.annotation.Nullable;

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
	 * @param tokenizer
	 * @throws IOException
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
			return new Token("---EMPTY---");
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
	@Nullable
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
	@Nullable
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
