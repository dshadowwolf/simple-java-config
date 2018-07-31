package com.keildraco.config.states;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;
import static java.io.StreamTokenizer.TT_WORD;

import java.io.StreamTokenizer;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

public class KeyValueParser extends AbstractParserBase {

	public KeyValueParser(final TypeFactory factory, final String name) {
		super(factory, null, name);
	}

	public KeyValueParser(final TypeFactory factory) {
		super(factory, null, "Well I'll Be Buggered");
	}

	@Override
	public ParserInternalTypeBase getState(final StreamTokenizer tok) {
		final int p = this.nextToken(tok);

		if (!this.errored() && p == TT_WORD && tok.sval.matches(IDENTIFIER_PATTERN)) {
			final String temp = tok.sval;
			if (this.peekToken(tok) == '(') {
				return this.factory.parseTokens("OPERATION", null, tok, temp);
			} else {
				return this.factory.getType(this.getParent(), this.name, temp, ItemType.IDENTIFIER);
			}
		} else if (!this.errored() && p != TT_WORD) {
			switch (p) {
				case StreamTokenizer.TT_EOF:
					Config.LOGGER.error(
							"Premature End of File while parsing a key-value pair, line %d",
							tok.lineno());
					this.setErrored();
					break;
				case '[':
					return this.factory.parseTokens("LIST", this.parent, tok, this.name);
				case '}':
					tok.pushBack();
					return EmptyType;
				default:
					Config.LOGGER.error(
							"Token of unexpected type %s found where TT_WORD expected, line %d",
							ttypeToString(p), tok.lineno());
			}
			tok.pushBack();
			return EmptyType;
		} else {
			Config.LOGGER.error("ERROR! ERROR! ERROR! - Error parsing at line " + tok.lineno());
			tok.pushBack();
			return EmptyType;
		}
	}
}
