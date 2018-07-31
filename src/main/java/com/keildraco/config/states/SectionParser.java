package com.keildraco.config.states;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;
import static java.io.StreamTokenizer.TT_EOF;
import static java.io.StreamTokenizer.TT_WORD;

import java.io.StreamTokenizer;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.SectionType;

public class SectionParser extends AbstractParserBase {

	private final SectionType section;

	public SectionParser(final TypeFactory factory) {
		super(factory, null, "ROOT");
		this.section = new SectionType(EmptyType, this.name);
	}

	public SectionParser(final TypeFactory factory, final SectionType parent, final String name) {
		super(factory, parent, name);
		this.section = new SectionType(parent, this.name);
	}

	public SectionParser(final TypeFactory factory, final String name) {
		super(factory, null, name);
		this.section = new SectionType(parent, this.name);
	}

	@Override
	public ParserInternalTypeBase getState(final StreamTokenizer tok) {
		String ident = "";
		while (this.nextToken(tok) != TT_EOF && !this.errored()) {
			int tt = getTokenType(tok);

			switch (tt) {
				case '=':
					if (ident.equals("")) {
						this.setErrored();
						Config.LOGGER.error(
								"Found a store operation (=) where I was expecting an identifier");
						return EmptyType;
					}
					this.getKeyValue(tok, ident);
					ident = "";
					break;
				case '{':
					this.getSection(tok, ident);
					break;
				case '}':
					return this.section;
				case -1:
					ident = tok.sval.trim();
					break;
				case -2:
				case -3:
				case -4:
				default:
					this.setErrored();
					Config.LOGGER.error("Found %s where it was not expected - this is an error",
							itToString(tt));
					return EmptyType;
			}
		}
		if (!this.errored()) {
			return this.section;
		}
		return EmptyType;
	}

	private void getSection(final StreamTokenizer tok, final String ident) {
		final ParserInternalTypeBase sk = this.factory.parseTokens("SECTION", this.section, tok,
				ident);
		if (EmptyType.equals(sk)) {
			this.setErrored();
		} else {
			this.section.addItem(sk);
		}
	}

	private void getKeyValue(final StreamTokenizer tok, final String ident) {
		final ParserInternalTypeBase kv = this.factory.parseTokens("KEYVALUE", this.section, tok,
				ident);
		if (EmptyType.equals(kv)) {
			this.setErrored();
		} else {
			this.section.addItem(kv);
		}
	}

	private String itToString(final int tt) {
		if (tt == -1) {
			return "an Identifier";
		}
		return String.format("'%c'", tt);
	}

	private static int getTokenType(final StreamTokenizer tok) {
		if (tok.ttype == TT_WORD) {
			if (tok.sval.matches(IDENTIFIER_PATTERN)) {
				return -1;
			}
			return -4;
		} else {
			return tok.ttype;
		}
	}
}
