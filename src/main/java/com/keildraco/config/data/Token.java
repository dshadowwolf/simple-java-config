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
	private final String val;

	/**
	 *
	 */
	private final TokenType type;

	/**
	 *
	 * @param valueIn
	 */
	public Token(final String valueIn) {
		this.val = valueIn;
		if (this.val.matches("^\\s*[a-zA-Z_]{1}[a-zA-Z0-9_]*\\s*$")) {
			this.type = TokenType.IDENTIFIER;
		} else {
			switch (this.val) {
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

	/**
	 *
	 * @return
	 */
	public String getValue() {
		return this.val;
	}

	/**
	 *
	 * @return
	 */
	public TokenType getType() {
		return this.type;
	}
}
