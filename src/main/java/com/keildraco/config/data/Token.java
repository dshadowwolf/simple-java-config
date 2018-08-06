package com.keildraco.config.data;

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
		this.value = valueIn;
		if (this.value.matches("^\\s*[a-zA-Z_]{1}[a-zA-Z0-9_]*\\s*$")) {
			this.type = TokenType.IDENTIFIER;
		} else {
			switch (this.value) {
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
				case "---EMPTY---":
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
