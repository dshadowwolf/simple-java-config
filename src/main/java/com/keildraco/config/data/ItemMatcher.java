package com.keildraco.config.data;

import com.keildraco.config.types.ParserInternalTypeBase;

import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;
import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

import com.keildraco.config.types.SectionType;

import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;

public class ItemMatcher {
	private final ParserInternalTypeBase thisItem;

	public static final ItemMatcher AlwaysFalse = new ItemMatcher(EmptyType) {
		@Override
		public boolean matches(final String name) {
			return false;
		}
	};

	public ItemMatcher(final ParserInternalTypeBase item) {
		this.thisItem = item;
	}

	public boolean matches(final String name) {
		final boolean tn = name.contains(".");
		final String bn = tn?name.substring(0, name.indexOf('.')):name;
		final String xn = tn?name.substring(name.indexOf('.')+1):"";

		return this.doMatch(this.thisItem.getType(),bn,xn);
	}

	private boolean doMatch(final ItemType type, final String bn, final String xn) {
		switch (type) {
		case IDENTIFIER:
			if (xn.length() > 0) return this.identMatches((IdentifierType) this.thisItem,xn) && this.thisItem.getName().equalsIgnoreCase(bn);
			else return this.identMatches((IdentifierType) this.thisItem,bn);
		case LIST:
			String matchN = bn;
			if (this.thisItem.getName().equalsIgnoreCase(bn) && xn.length() > 0) matchN = xn;
			return this.listMatchesAny(matchN);
		case OPERATION:
			return this.operatorMatches(xn.length()>0?String.format("%s.%s", bn, xn):bn);
		case SECTION:
			if (xn.length() > 0) return (new ItemMatcher(this.thisItem.get(bn))).matches(xn);
			return this.sectionMatches(bn);
		default:
			return false;
		}
	}

	private boolean sectionMatches(final SectionType sec, final String name) {
		return sec.has(name);
	}

	private boolean sectionMatches(final String name) {
		return this.sectionMatches((SectionType) this.thisItem, name);
	}

	private boolean matchOperator(final OperationType op, final String itemName, final String valueName) {
		String matchName = itemName;
		if (op.getName().equalsIgnoreCase(itemName) && valueName.length() > 0) {
			matchName = valueName;
		}

		if (op.getOperator() == '!') return !op.getValue().equalsIgnoreCase(matchName);
		else if (op.getOperator() == '~') return op.getValue().equalsIgnoreCase(matchName);
		return true;
	}

	private boolean operatorMatches(final OperationType op, final String name) {
		if (name.indexOf('.') > 0) {
			final String in = name.substring(0, name.indexOf('.'));
			final String vn = name.substring(name.indexOf('.')+1);
			return this.matchOperator(op, in, vn);
		} else {
			return this.matchOperator(op, name, "");
		}
	}

	private boolean operatorMatches(final String name) {
		return this.operatorMatches((OperationType) this.thisItem, name);
	}

	private boolean identMatches(final IdentifierType ident, final String name) {
		return ident.getName().equalsIgnoreCase(name) || ident.getValue().equalsIgnoreCase(name);
	}

	private boolean listMatchesAny(final ListType theList, final String name) {
		return theList.has(name);
	}

	private boolean listMatchesAny(final String name) {
		return this.listMatchesAny((ListType) this.thisItem,name);
	}
}
