package com.keildraco.config.factory;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.util.Deque;
import java.util.LinkedList;

public class Tokenizer {
	private final StreamTokenizer baseTokenizer;
	private final Deque<Token> tokens;

	public enum TokenType {
		IDENTIFIER, STORE, OPEN_BRACE, CLOSE_BRACE, OPEN_PARENS, CLOSE_PARENS, TILDE, NOT, OPEN_LIST, CLOSE_LIST, SEPERATOR, UNKNOWN;
	}

	public class Token {
		private final String val;
		private final TokenType type;
		
		public Token(String val) {
			this.val = val;
			if(this.val.matches("^\\s*[a-zA-Z_]{1}[a-zA-Z0-9_]*\\s*$")) {
				this.type = TokenType.IDENTIFIER;
			} else {
				switch(this.val) {
				case "=":
					this.type = TokenType.STORE;
					break;
				case "{":
					this.type = TokenType.OPEN_BRACE;
					break;
				case "}":
					this.type = TokenType.CLOSE_BRACE;
					break;
				case "(":
					this.type = TokenType.OPEN_PARENS;
					break;
				case ")":
					this.type = TokenType.CLOSE_PARENS;
					break;
				case "~":
					this.type = TokenType.TILDE;
					break;
				case "!":
					this.type = TokenType.NOT;
					break;
				case "[":
					this.type = TokenType.OPEN_LIST;
					break;
				case "]":
					this.type = TokenType.CLOSE_LIST;
					break;
				case ",":
					this.type = TokenType.SEPERATOR;
					break;
				default:
					this.type = TokenType.UNKNOWN;
					break;
				}
			}
		}
		
		public String getValue() {
			return this.val;
		}
		
		public TokenType getType() {
			return this.type;
		}
	}
	
	public Tokenizer(StreamTokenizer tok) throws IOException {
		this.baseTokenizer = tok;
		this.tokens = new LinkedList<>();
		this.baseTokenizer.slashSlashComments(true);
		this.baseTokenizer.slashStarComments(true);
		this.baseTokenizer.commentChar('#');
		this.baseTokenizer.wordChars('_', '_');
		this.baseTokenizer.wordChars('-', '-');
		
		int p;
		while((p = this.baseTokenizer.nextToken()) != StreamTokenizer.TT_EOF) {
			if(p == StreamTokenizer.TT_WORD) this.tokens.addLast(new Token(this.baseTokenizer.sval));
			else if(p != StreamTokenizer.TT_NUMBER && p != StreamTokenizer.TT_EOL) this.tokens.addLast(new Token(String.format("%c", p)));
			else this.tokens.addLast(new Token(String.format("-%s-%c-", this.baseTokenizer.sval, p>0&&p<127?p:0x01)));
		}
	}

	public Token nextToken() {
		return this.tokens.pop();
	}
	
	public boolean hasNext() {
		return !this.tokens.isEmpty();
	}
	
	public Token peekToken() {
		if(this.tokens.isEmpty()) return null;
		Token k = this.tokens.pop();
		Token rv = this.tokens.peek();
		this.tokens.push(k);
		return rv;
	}
	
	public Token peek() {
		return this.tokens.peek();
	}
	
	public void pushBack(Token token) {
		this.tokens.push(token);
	}
}
