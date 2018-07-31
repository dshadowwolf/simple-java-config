package com.keildraco.config.states;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

import java.io.StreamTokenizer;
import java.util.Collections;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

/**
 * @author Daniel Hazelton
 *
 */
public class ListParser extends AbstractParserBase {

	/**
	 *
	 * @param factory
	 * @param name
	 */
	public ListParser(final TypeFactory factory, final String name) {
		super(factory, null, name);
	}

	public ListParser(final TypeFactory factory) {
		super(factory, null, "Well I'll Be Buggered");
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.keildraco.config.states.IStateParser#getState(java.io.StreamTokenizer)
	 */
	@Override
	public ParserInternalTypeBase getState(final StreamTokenizer tok) {
		int p;
		final Deque<ParserInternalTypeBase> store = new LinkedList<>();
		String ident;
		while ((p = this.nextToken(tok)) != StreamTokenizer.TT_EOF && p != ']') {
			if (p == '[') {
				continue;
			}
			if (!this.errored && p == StreamTokenizer.TT_WORD
					&& tok.sval.matches(IDENTIFIER_PATTERN)) {
				ident = tok.sval;
				final ParserInternalTypeBase temp = this.getToken(tok, ident);
				temp.setName(ident);
				store.push(temp);
			} else if (p == StreamTokenizer.TT_WORD) {
				Config.LOGGER.fatal(
						"Error loading list, did not find TT_WORD matching %s where expected (%s found)",
						IDENTIFIER_PATTERN, tok.sval);
				return EmptyType;
			}
		}

		final List<ParserInternalTypeBase> l = store.stream().collect(Collectors.toList());
		Collections.reverse(l);
		return l.contains(EmptyType) ? EmptyType : new ListType(this.name, l);
	}

	private ParserInternalTypeBase getToken(final StreamTokenizer tok, final String ident) {
		final int n = this.peekToken(tok);
		if (n != StreamTokenizer.TT_WORD && (n == ',' || n == ']')) {
			return this.factory.getType(this.getParent(), this.getName(), ident,
					ItemType.IDENTIFIER);
		} else if (n == '(') {
			return this.factory.parseTokens("OPERATION", this.getParent(), tok, ident);
		}
		this.setErrored();
		return EmptyType;
	}
}
