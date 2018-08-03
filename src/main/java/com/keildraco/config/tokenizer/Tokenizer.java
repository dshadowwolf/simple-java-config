package com.keildraco.config.tokenizer;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Deque;
import java.util.LinkedList;

import com.keildraco.config.data.Token;

public final class Tokenizer {

	private final StreamTokenizer baseTokenizer;
	private final Deque<Token> tokens;

	public Tokenizer(final StreamTokenizer tok) throws IOException {
		this.baseTokenizer = tok;
		this.tokens = new LinkedList<>();
		this.baseTokenizer.slashSlashComments(true);
		this.baseTokenizer.slashStarComments(true);
		this.baseTokenizer.commentChar('#');
		this.baseTokenizer.wordChars('_', '_');
		this.baseTokenizer.wordChars('-', '-');

		int p;
		while ((p = this.baseTokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
			if (p == StreamTokenizer.TT_WORD) {
				this.tokens.addLast(new Token(this.baseTokenizer.sval));
			} else {
				this.tokens.addLast(new Token(String.format("%c", p)));
			}
		}
	}

	public Token nextToken() {
		return this.tokens.pop();
	}

	public boolean hasNext() {
		return !this.tokens.isEmpty();
	}

	public Token peekToken() {
		if (this.tokens.isEmpty()) {
			return null;
		}

		Token k = this.tokens.pop();
		Token rv = this.tokens.peek();
		this.tokens.push(k);
		return rv;
	}

	public Token peek() {
		return this.tokens.peek();
	}

	public void pushBack(final Token token) {
		this.tokens.push(token);
	}
}
