package com.keildraco.config.data;

import com.keildraco.config.interfaces.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.SectionType;
import static com.keildraco.config.Config.EMPTY_TYPE;

/**
 *
 * @author Daniel Hazelton
 *
 */
public class ItemMatcher {

	/**
	 *
	 */
	private final ParserInternalTypeBase thisItem;

	/**
	 *
	 */
	public static final ItemMatcher ALWAYS_FALSE = new ItemMatcher(EMPTY_TYPE) {
		@Override
		public boolean matches(final String name) {
			return false;
		}
	};

	/**
	 *
	 * @param item
	 */
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
		String baseName = name;
		String extendedNameData = "";

		if (tn) {
			baseName = name.substring(0, name.indexOf('.'));
			extendedNameData = name.substring(name.indexOf('.') + 1);
		}

		return this.doMatch(this.thisItem.getType(), baseName, extendedNameData);
	}

	/**
	 *
	 * @param type
	 * @param baseName
	 * @param extendedNameData
	 * @return
	 */
	private boolean doMatch(final ItemType type, final String baseName, final String extendedNameData) {
		switch (type) {
			case IDENTIFIER:
				return this.identMatcher(baseName, extendedNameData);
			case LIST:
				return this.listMatcher(baseName, extendedNameData);
			case OPERATION:
				return this.operatorMatches(baseName);
			case SECTION:
				return this.sectionMatcher(baseName, extendedNameData);
			default:
				return false;
		}
	}

	/**
	 *
	 * @param baseName
	 * @param extendedNameData
	 * @return
	 */
	private boolean sectionMatcher(final String baseName, final String extendedNameData) {
		if (this.thisItem.getName().equalsIgnoreCase(baseName)) {
			// we match the base name itself, so we have to see if we can split the extended name or
			// don't need to and re-match
			if (!extendedNameData.isEmpty()) {
				return this.matches(extendedNameData);
			} else {
				return true;
			}
		} else if (this.thisItem.has(baseName)) {
			if (!extendedNameData.isEmpty()) {
				return new ItemMatcher(this.thisItem.get(baseName)).matches(extendedNameData);
			} else {
				return true;
			}
		}

		// blargh ? Final chance, maybe we've found a loophole!
		return this.sectionMatches(baseName);
	}

	/**
	 *
	 * @param baseName
	 * @param extendedNameData
	 * @return
	 */
	private boolean listMatcher(final String baseName, final String extendedNameData) {
		if (extendedNameData.isEmpty()) {
			// above all else we're only, actually, into the list here...
			return this.listMatchesAny(baseName);
		}
		// if we have an extendedNameData value, its likely we're looking for an operator
		if (this.thisItem.has(baseName)) {
			return new ItemMatcher(this.thisItem.get(baseName)).matches(extendedNameData);
		}

		return Boolean.FALSE;
	}

	/**
	 *
	 * @param baseName
	 * @param extendedNameData
	 * @return
	 */
	private boolean identMatcher(final String baseName, final String extendedNameData) {
		if (!extendedNameData.isEmpty()) {
			return this.identMatches((IdentifierType) this.thisItem, baseName, extendedNameData);
		} else {
			return this.identMatches((IdentifierType) this.thisItem, baseName);
		}
	}

	/**
	 *
	 * @param baseName
	 * @return
	 */
	private boolean operatorMatches(final String baseName) {
		// at this point our item is an operator, so we should only have 'baseName'

		final OperationType op = (OperationType) this.thisItem;
		final int oper = op.getOperator();
		if (oper == '!') {
			return !op.getValueRaw().equalsIgnoreCase(baseName);
		} else if (oper == '~') {
			return op.getValueRaw().equalsIgnoreCase(baseName);
		}

		return false;
	}

	/**
	 *
	 * @param section
	 * @param name
	 * @return
	 */
	private boolean sectionMatches(final SectionType section, final String name) {
		return section.has(name);
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	private boolean sectionMatches(final String name) {
		return this.sectionMatches((SectionType) this.thisItem, name);
	}

	/**
	 *
	 * @param ident
	 * @param value
	 * @return
	 */
	private boolean identMatches(final IdentifierType ident, final String value) {
		return ident.getValueRaw().equalsIgnoreCase(value);
	}

	/**
	 *
	 * @param ident
	 * @param name
	 * @param value
	 * @return
	 */
	private boolean identMatches(final IdentifierType ident, final String name,
			final String value) {
		return ident.getName().equalsIgnoreCase(name)
				&& ident.getValueRaw().equalsIgnoreCase(value);
	}

	/**
	 *
	 * @param theList
	 * @param name
	 * @return
	 */
	private boolean listMatchesAny(final ListType theList, final String name) {
		return theList.has(name);
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	private boolean listMatchesAny(final String name) {
		return this.listMatchesAny((ListType) this.thisItem, name);
	}
}
