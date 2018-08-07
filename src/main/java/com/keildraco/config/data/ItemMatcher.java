package com.keildraco.config.data;

import com.keildraco.config.interfaces.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.SectionType;

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

		return this.doesItemMatch(this.thisItem.getType(), baseName, extendedNameData);
	}

	/**
	 *
	 * @param type
	 * @param baseName
	 * @param extendedNameData
	 * @return
	 */
	private boolean doesItemMatch(final ItemType type, final String baseName,
			final String extendedNameData) {
		switch (type) {
			case IDENTIFIER:
				return this.doesIdentifierMatch(baseName, extendedNameData);
			case LIST:
				return this.doesListMatch(baseName, extendedNameData);
			case OPERATION:
				return this.doesOperatorMatch(baseName);
			case SECTION:
				return this.doesSectionMatch(baseName, extendedNameData);
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
	private boolean doesSectionMatch(final String baseName, final String extendedNameData) {
		if (this.thisItem.getName().equalsIgnoreCase(baseName) && !extendedNameData.isEmpty()) {
			// we match the base name itself, so we have to see if we can split the extended name or
			// don't need to and re-match
			return this.matches(extendedNameData);
		} else if (this.thisItem.has(baseName) && !extendedNameData.isEmpty()) {
			return new ItemMatcher(this.thisItem.get(baseName)).matches(extendedNameData);
		} else if (extendedNameData.isEmpty() && (this.thisItem.has(baseName)
				|| this.thisItem.getName().equalsIgnoreCase(baseName))) {
			return true;
		} else {
			// blargh ? Final chance, maybe we've found a loophole!
			return this.doesSectionMatch(baseName);
		}
	}

	/**
	 *
	 * @param baseName
	 * @param extendedNameData
	 * @return
	 */
	private boolean doesListMatch(final String baseName, final String extendedNameData) {
		if (extendedNameData.isEmpty()) {
			// above all else we're only, actually, into the list here...
			return this.doesAnyItemInMyListMatch(baseName);
		} else if (this.thisItem.has(baseName)) {
			// if we have an extendedNameData value, its likely we're looking for an operator
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
	private boolean doesIdentifierMatch(final String baseName, final String extendedNameData) {
		if (!extendedNameData.isEmpty()) {
			return doesThisIdentifierMatch((IdentifierType) this.thisItem, baseName,
					extendedNameData);
		} else {
			return doesThisIdentifierMatchByNameOnly((IdentifierType) this.thisItem, baseName);
		}
	}

	/**
	 *
	 * @param baseName
	 * @return
	 */
	private boolean doesOperatorMatch(final String baseName) {
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
	private static boolean doesThisSectionMatch(final SectionType section, final String name) {
		return section.has(name);
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	private boolean doesSectionMatch(final String name) {
		return doesThisSectionMatch((SectionType) this.thisItem, name);
	}

	/**
	 *
	 * @param ident
	 * @param value
	 * @return
	 */
	private static boolean doesThisIdentifierMatchByNameOnly(final IdentifierType ident,
			final String value) {
		return ident.getValueRaw().equalsIgnoreCase(value);
	}

	/**
	 *
	 * @param ident
	 * @param name
	 * @param value
	 * @return
	 */
	private static boolean doesThisIdentifierMatch(final IdentifierType ident, final String name,
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
	private static boolean doesAnyItemInThisListMatch(final ListType theList, final String name) {
		return theList.has(name);
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	private boolean doesAnyItemInMyListMatch(final String name) {
		return doesAnyItemInThisListMatch((ListType) this.thisItem, name);
	}
}
