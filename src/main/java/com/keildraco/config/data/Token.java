package com.keildraco.config.data;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class Token {

	/**
	 *
	 */
	private final String value;

	/**
	 *
	 */
	private final TokenType type;

	/**
	 *
	 * @param valueIn
	 */
	public Token(final String valueIn) {
		final Matcher m = Pattern.compile("^\\s*[a-zA-Z_]{1}[a-zA-Z0-9_]*\\s*$").matcher(valueIn);
		this.value = valueIn;
		if (m.matches()) {
			this.type = TokenType.IDENTIFIER;
		} else {
			switch (this.value) {
				case Constants.STOREOPERATOR:
					this.type = TokenType.STORE;
					break;
				case Constants.OPENBRACE:
					this.type = TokenType.OPEN_BRACE;
					break;
				case Constants.CLOSEBRACE:
					this.type = TokenType.CLOSE_BRACE;
					break;
				case Constants.OPENPARENS:
					this.type = TokenType.OPEN_PARENS;
					break;
				case Constants.CLOSEPARENS:
					this.type = TokenType.CLOSE_PARENS;
					break;
				case Constants.IGNOREASSTRING:
					this.type = TokenType.TILDE;
					break;
				case Constants.NOTASSTRING:
					this.type = TokenType.NOT;
					break;
				case Constants.OPENLIST:
					this.type = TokenType.OPEN_LIST;
					break;
				case Constants.CLOSELIST:
					this.type = TokenType.CLOSE_LIST;
					break;
				case Constants.LISETSEPERATOR:
					this.type = TokenType.SEPERATOR;
					break;
				case Constants.TOKENEMPTY:
					this.type = TokenType.EMPTY;
					break;
				default:
					this.type = TokenType.UNKNOWN;
					break;
			}
		}
	}

	/**
	 *
	 * @return
	 */
	public String getValue() {
		return this.value;
	}

	/**
	 *
	 * @return
	 */
	public TokenType getType() {
		return this.type;
	}

	/**
	 *
	 * @return
	 */
	public boolean isEmpty() {
		return this.type.equals(TokenType.EMPTY);
	}
}
