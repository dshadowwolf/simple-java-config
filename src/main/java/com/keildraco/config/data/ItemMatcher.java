package com.keildraco.config.data;

import static com.keildraco.config.interfaces.ParserInternalTypeBase.EMPTY_TYPE;

import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.SectionType;

public class ItemMatcher {

	private final ParserInternalTypeBase thisItem;

	public static final ItemMatcher ALWAYS_FALSE = new ItemMatcher(EMPTY_TYPE) {

		@Override
		public boolean matches(final String name) {
			return false;
		}
	};

	public ItemMatcher(final ParserInternalTypeBase item) {
		this.thisItem = item;
	}

	/**
	 *
	 * @param name
	 *            Possibly dotted-notation name to match to this particular piece
	 * @return true if the name has a match - either short or deep - to this part
	 */
	public boolean matches(final String name) {
		final boolean tn = name.contains(".");
		String bn = name;
		String xn = "";

		if (tn) {
			bn = name.substring(0, name.indexOf('.'));
			xn = name.substring(name.indexOf('.') + 1);
		}

		return this.doMatch(this.thisItem.getType(), bn, xn);
	}

	private boolean doMatch(final ItemType type, final String bn, final String xn) {
		switch (type) {
			case IDENTIFIER:
				return this.identMatcher(bn, xn);
			case LIST:
				return this.listMatcher(bn, xn);
			case OPERATION:
				return this.operatorMatches(bn);
			case SECTION:
				return this.sectionMatcher(bn, xn);
			default:
				return false;
		}
	}

	private boolean sectionMatcher(final String bn, final String xn) {
		if (this.thisItem.getName().equalsIgnoreCase(bn)) {
			// we match the base name itself, so we have to see if we can split the extended name or
			// don't need to and re-match
			if (xn.length() > 0) {
				return this.matches(xn);
			} else {
				return true;
			}
		} else if (this.thisItem.has(bn)) {
			if (xn.length() > 0) {
				return new ItemMatcher(this.thisItem.get(bn)).matches(xn);
			} else {
				return true;
			}
		}

		// blargh ? Final chance, maybe we've found a loophole!
		return this.sectionMatches(bn);
	}

	private boolean listMatcher(final String bn, final String xn) {
		if (xn.length() == 0) {
			// above all else we're only, actually, into the list here...
			return this.listMatchesAny(bn);
		}
		// if we have an xn value, its likely we're looking for an operator
		if (this.thisItem.has(bn)) {
			return new ItemMatcher(this.thisItem.get(bn)).matches(xn);
		}

		return Boolean.FALSE;
	}

	private boolean identMatcher(final String bn, final String xn) {
		if (xn.length() > 0) {
			return this.identMatches((IdentifierType) this.thisItem, bn, xn);
		} else {
			return this.identMatches((IdentifierType) this.thisItem, bn);
		}
	}

	private boolean operatorMatches(final String bn) {
		// at this point our item is an operator, so we should only have 'bn'

		OperationType op = (OperationType) this.thisItem;
		int oper = op.getOperator();
		if (oper == '!') {
			return !op.getValueRaw().equalsIgnoreCase(bn);
		} else if (oper == '~') {
			return op.getValueRaw().equalsIgnoreCase(bn);
		}

		return false;
	}

	private boolean sectionMatches(final SectionType sec, final String name) {
		return sec.has(name);
	}

	private boolean sectionMatches(final String name) {
		return this.sectionMatches((SectionType) this.thisItem, name);
	}

	private boolean identMatches(final IdentifierType ident, final String value) {
		return ident.getValueRaw().equalsIgnoreCase(value);
	}

	private boolean identMatches(final IdentifierType ident, final String name,
			final String value) {
		return ident.getName().equalsIgnoreCase(name)
				&& ident.getValueRaw().equalsIgnoreCase(value);
	}

	private boolean listMatchesAny(final ListType theList, final String name) {
		return theList.has(name);
	}

	private boolean listMatchesAny(final String name) {
		return this.listMatchesAny((ListType) this.thisItem, name);
	}
}
