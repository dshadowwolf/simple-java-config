package com.keildraco.config.data;

import java.util.Locale;

import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;

/**
 *
 * @author Daniel Hazelton
 *
 */
public class ItemMatcher {

	/**
	 * Internal reference to the {@link com.keildraco.config.interfaces.ParserInternalTypeBase
	 * ParserInternalTypeBase} object that this ItemMatcher was created to search through.
	 */
	private final ParserInternalTypeBase thisItem;

	/**
	 * <p>
	 * Create and return a generic interface for matching a specifically separated key (if such
	 * separator exists) to the {@link com.keildraco.config.interfaces.ParserInternalTypeBase
	 * ParserInternalTypeBase} object, if at all possible.
	 *
	 * @param item
	 *            The {@link com.keildraco.config.interfaces.ParserInternalTypeBase
	 *            ParserInternalTypeBase} object that this object will match against.
	 */
	public ItemMatcher(final ParserInternalTypeBase item) {
		this.thisItem = item;
	}

	/**
	 * Does the given name match the given object in any manner?
	 *
	 * @param name
	 *            Possibly dotted-notation name to match to this particular piece.
	 * @return true if the name has a match - either short or deep - to this part.
	 */
	public boolean matches(final String name) {
		final boolean tn = name.contains(".");
		String baseName = name;
		String extendedNameData = "";

		if (tn) {
			baseName = name.substring(0, name.indexOf(Constants.KEYSEPARATOR))
					.toLowerCase(Locale.getDefault());
			extendedNameData = name.substring(name.indexOf(Constants.KEYSEPARATOR) + 1)
					.toLowerCase(Locale.getDefault());
		}

		return this.doesItemMatch(this.thisItem.getType(), baseName, extendedNameData);
	}

	/**
	 * Internal dispatch routine for doing the matching.
	 *
	 * @param type
	 *            <p>
	 *            Type of item being matched. Neither
	 *            {@link com.keildraco.config.data.ItemType.INVALID ItemType.INVALID} or
	 *            {@link com.keildraco.config.data.ItemType.EMPTY ItemType.EMPTY} items will match -
	 *            in those cases the result is always false.
	 * @param baseName
	 *            <p>
	 *            In the case that the name could not be split on a separator, the entirety of the
	 *            name to match. Otherwise this is just the highest level part of the name.
	 * @param extendedNameData
	 *            <p>
	 *            The unsplit remains, if such exist, or the empty string ("").
	 * @return boolean "true" if the item is found to meet the given data, "false" if it does not.
	 */
	private boolean doesItemMatch(final ItemType type, final String baseName,
			final String extendedNameData) {
		switch (type) {
			case IDENTIFIER:
				return this.doesIdentifierMatch(baseName, extendedNameData);
			case LIST:
				return this.doesListMatch(baseName, extendedNameData);
			case OPERATION:
				return this.doesOperationMatch(baseName);
			case SECTION:
				return this.doesSectionMatch(baseName, extendedNameData);
			case BASIC_RESULT:
				return this.basicResultMatch(baseName, extendedNameData);
			default:
				return false;
		}
	}

	private boolean basicResultMatch(String baseName, String extendedNameData) {
		if(thisItem.has(baseName)) {
			if(extendedNameData.isEmpty()) return true;
			return new ItemMatcher(thisItem.get(baseName)).matches(extendedNameData);
		}
		return false;
	}

	/**
	 * Internal routine for matching a {@link com.keildraco.config.data.ItemType.SECTION section}.
	 *
	 * @param baseName
	 *            <p>
	 *            In the case that the name could not be split on a separator, the entirety of the
	 *            name to match. Otherwise this is just the highest level part of the name.
	 * @param extendedNameData
	 *            <p>
	 *            The unsplit remains, if such exist, or the empty string ("").
	 * @return boolean "true" if the item is found to meet the given data, "false" if it does not.
	 */
	private boolean doesSectionMatch(final String baseName, final String extendedNameData) {
		if(!extendedNameData.isEmpty()) {
			if (this.thisItem.getName().equals(baseName)) {
				return this.matches(extendedNameData);
			} else if (this.thisItem.has(baseName)) {
				return new ItemMatcher(this.thisItem.get(baseName)).matches(extendedNameData);
			}
		} else if (this.thisItem.has(baseName)) {
			return true;
		}
		
		return false;
	}

	/**
	 * Internal routine for matching a {@link com.keildraco.config.data.ItemType.LIST list}.
	 *
	 * @param baseName
	 *            <p>
	 *            In the case that the name could not be split on a separator, the entirety of the
	 *            name to match. Otherwise this is just the highest level part of the name.
	 * @param extendedNameData
	 *            <p>
	 *            The unsplit remains, if such exist, or the empty string ("").
	 * @return boolean "true" if the item is found to meet the given data, "false" if it does not.
	 */
	private boolean doesListMatch(final String baseName, final String extendedNameData) {
		if (extendedNameData.isEmpty()) {
			// above all else we're only, actually, into the list here...
			return this.doesAnyItemInMyListMatch(baseName);
		} else if (this.thisItem.has(baseName)) {
			// if we have an extendedNameData value, its likely we're looking for an operator
			return new ItemMatcher(this.thisItem.get(baseName)).matches(extendedNameData);
		}

		return false;
	}

	/**
	 * Internal routine for matching an {@link com.keildraco.config.data.ItemType.IDENTIFIER
	 * identifier}. Also used to match a Key-Value pair, which is represented inside the parse-tree
	 * as an Identifier.
	 *
	 * @param baseName
	 *            <p>
	 *            In the case that the name could not be split on a separator, the entirety of the
	 *            name to match. Otherwise this is just the highest level part of the name.
	 * @param extendedNameData
	 *            <p>
	 *            The unsplit remains, if such exist, or the empty string ("").
	 * @return boolean "true" if the item is found to meet the given data, "false" if it does not.
	 */
	private boolean doesIdentifierMatch(final String baseName, final String extendedNameData) {
		if (!extendedNameData.isEmpty()) {
			return doesThisIdentifierMatch((IdentifierType) this.thisItem, baseName,
					extendedNameData);
		} else {
			return doesThisIdentifierMatchByValueOnly((IdentifierType) this.thisItem, baseName);
		}
	}

	/**
	 * Internal routine for matching a {@link com.keildraco.config.data.ItemType.OPERATION
	 * operation}.
	 *
	 * @param baseName
	 *            <p>
	 *            In the case that the name could not be split on a separator, the entirety of the
	 *            name to match. Otherwise this is just the highest level part of the name.
	 * @return boolean "true" if the item is found to meet the given data, "false" if it does not.
	 */
	private boolean doesOperationMatch(final String baseName) {
		// at this point our item is an operator, so we should only have 'baseName'

		final OperationType op = (OperationType) this.thisItem;
		final int oper = op.getOperator();
		if (oper == (int) Constants.NOTOPERATOR) {
			return !op.getValueRaw().equals(baseName);
		} else if (oper == (int) Constants.IGNOREOPERATOR) {
			return op.getValueRaw().equals(baseName);
		}

		return false;
	}

	/**
	 * One of the final matching routines for {@link com.keildraco.config.data.ItemType.IDENTIFIER
	 * identifiers}. This routine, specifically, will match for the "Value" side of a key-value
	 * pair.
	 *
	 * @param ident
	 *            <p>
	 *            The {@link com.keildraco.config.types.IdentifierType identifier} to match against.
	 * @param value
	 *            <p>
	 *            The {@link java.lang.String String} value to match for.
	 * @return boolean "true" if the item is found to meet the given data, "false" if it does not.
	 */
	private static boolean doesThisIdentifierMatchByValueOnly(final IdentifierType ident,
			final String value) {
		return ident.getValueRaw().equals(value);
	}

	/**
	 * One of the final matching routines for {@link com.keildraco.config.data.ItemType.IDENTIFIER
	 * identifiers}. This routine, specifically, will match for both the "Key" and "Value" sides of
	 * a key-value pair.
	 *
	 * @param ident
	 *            <p>
	 *            The {@link com.keildraco.config.types.IdentifierType identifier} to match against.
	 * @param name
	 *            <p>
	 *            The {@link java.lang.String String} name ("Key") to match for.
	 * @param value
	 *            <p>
	 *            The {@link java.lang.String String} value to match for.
	 * @return boolean "true" if the item is found to meet the given data, "false" if it does not.
	 */
	private static boolean doesThisIdentifierMatch(final IdentifierType ident, final String name,
			final String value) {
		return ident.getName().equals(name) && ident.getValueRaw().equals(value);
	}

	/**
	 * One of the terminal end-points for matching of a
	 * {@link com.keildraco.config.data.ItemType.LIST list}. This routine appears to, currently, be
	 * broken.
	 *
	 * @param theList
	 *            <p>
	 *            The {@link com.keildraco.config.types.ListType list} to search for a match.
	 * @param name
	 *            <p>
	 *            Item name to match for.
	 * @return boolean "true" if the item is found to meet the given data, "false" if it does not.
	 */
	private static boolean doesAnyItemInThisListMatch(final ListType theList, final String name) {
		// FIXED: Doesn't appear to need special handling of operations
		return theList.has(name);
	}

	/**
	 * Try to match the {@link com.keildraco.config.data.ItemType.LIST list} that is held by this
	 * ItemMatcher directly.
	 *
	 * @param name
	 *            <p>
	 *            The name to match against the list.
	 * @return boolean "true" if the item is found to meet the given data, "false" if it does not.
	 */
	private boolean doesAnyItemInMyListMatch(final String name) {
		return doesAnyItemInThisListMatch((ListType) this.thisItem, name);
	}
}
