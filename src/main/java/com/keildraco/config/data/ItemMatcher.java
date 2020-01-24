package com.keildraco.config.data;

import java.util.Locale;

import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.SectionType;
import com.keildraco.config.data.BooleanMatch;

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

		return this.doesItemMatch(this.thisItem.getType(), baseName, extendedNameData).getValue();
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
	private BooleanMatch doesItemMatch(final ItemType type, final String baseName,
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
				return BooleanMatch.FALSE;
		}
	}

	private BooleanMatch basicResultMatch(String baseName, String extendedNameData) {
		if(thisItem.has(baseName)) {
			if(extendedNameData.isEmpty()) return BooleanMatch.TRUE;
			return internalMatchType(thisItem.get(baseName), extendedNameData);
		}
		return BooleanMatch.FALSE;
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
	private BooleanMatch doesSectionMatch(final String baseName, final String extendedNameData) {
		return doesThisSectionMatch((SectionType)this.thisItem, baseName, extendedNameData);
	}

	private static BooleanMatch doMatchReturnCombination(final BooleanMatch base, final BooleanMatch all) {
		if (base == BooleanMatch.FORCED_TRUE || base == BooleanMatch.FORCED_FALSE || base == BooleanMatch.TRUE) return base;
		else return all;
	}
	
	private static BooleanMatch doesThisSectionMatch(final SectionType item, final String baseName, final String extendedNameData) {
		BooleanMatch baseReturn = BooleanMatch.FALSE;
		BooleanMatch allReturn = BooleanMatch.FALSE;
		
		if(!extendedNameData.isEmpty()) {
			if (item.getName().equals(baseName)) {
				baseReturn = internalMatchType(item, extendedNameData);
			} else if (item.has(baseName)) {
				baseReturn = internalMatchType(item.get(baseName), extendedNameData);
			}
			if(item.has(Constants.ALL_KEY)) {
				allReturn = internalMatchType(item.get(Constants.ALL_KEY), extendedNameData);
			}
		} else if (item.has(baseName)) {
			baseReturn = BooleanMatch.TRUE;
			if(item.has(Constants.ALL_KEY)) {
				allReturn = internalMatchType(item.get(Constants.ALL_KEY), baseName);
			}
		}
		
		return doMatchReturnCombination(baseReturn, allReturn);
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
	private BooleanMatch doesListMatch(final String baseName, final String extendedNameData) {
		if (extendedNameData.isEmpty()) {
			// above all else we're only, actually, into the list here...
			return this.doesAnyItemInMyListMatch(baseName);
		} else if (this.thisItem.has(baseName)) {
			// if we have an extendedNameData value, its likely we're looking for an operator
			return internalMatchType(this.thisItem.get(baseName), extendedNameData);
		}

		return BooleanMatch.FALSE;
	}

	private static BooleanMatch internalMatchType(final ParserInternalTypeBase type, final String name) {
		final int index = name.indexOf(Constants.KEYSEPARATOR);
		String baseName = name;
		String extendedNameData = "";
		if(index > 0) {
			baseName = name.substring(0, index);
			extendedNameData = name.substring(index+1);
		} else if(index == 0) {
			throw new IllegalArgumentException("query keys cannot start with "+Constants.KEYSEPARATOR);
		}
		
		switch (type.getType()) {
			case IDENTIFIER:
				return doesThisIdentifierMatch((IdentifierType)type, baseName, extendedNameData);
			case LIST:
				return doesAnyItemInThisListMatch((ListType)type, name);
			case OPERATION:
				return doesThisOperationMatch((OperationType)type, baseName, extendedNameData);
			case SECTION:
				return doesThisSectionMatch((SectionType)type, baseName, extendedNameData);
			default:
				return BooleanMatch.FALSE;
		}
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
	private BooleanMatch doesIdentifierMatch(final String baseName, final String extendedNameData) {
		return doesThisIdentifierMatch((IdentifierType) this.thisItem, baseName, extendedNameData);
	}

	private static BooleanMatch doesThisIdentifierMatch(final IdentifierType ident, final String baseName, final String extendedNameData) {
		if (!extendedNameData.isEmpty()) {
			return doesThisIdentifierMatchByNameAndValue(ident, baseName,
					extendedNameData);
		} else {
			return doesThisIdentifierMatchByNameOrValue(ident, baseName);
		}
	}
	
	private static BooleanMatch doesThisOperationMatch(final OperationType operation, final String itemName, final String value) {
		final int oper = operation.getOperator();
		if(!operation.getName().equalsIgnoreCase(itemName)) {
			return BooleanMatch.FALSE;
		}
		
		if (oper == (int) Constants.NOTOPERATOR) {
			if(operation.getValueRaw().equals(value)) {
				return BooleanMatch.FORCED_FALSE;
			}
			return BooleanMatch.TRUE;
		} else if (oper == (int) Constants.IGNOREOPERATOR) {
			if(operation.getValueRaw().equals(value)) {
				return BooleanMatch.FORCED_TRUE;
			}
			return BooleanMatch.FALSE;
		}

		return BooleanMatch.FALSE;
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
	private BooleanMatch doesOperationMatch(final String baseName) {
		// at this point our item is an operator, so we should only have 'baseName'
		return doesThisOperationMatch((OperationType)this.thisItem, this.thisItem.getName(), baseName);
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
	private static BooleanMatch doesThisIdentifierMatchByNameOrValue(final IdentifierType ident,
			final String value) {
		if(ident.getValueRaw().equals(value) || ident.getName().equalsIgnoreCase(value)) {
			return BooleanMatch.TRUE;
		}
		return BooleanMatch.FALSE;
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
	private static BooleanMatch doesThisIdentifierMatchByNameAndValue(final IdentifierType ident, final String name,
			final String value) {
		if(ident.getName().equals(name) && ident.getValueRaw().equals(value)) {
			return BooleanMatch.TRUE;
		}
		return BooleanMatch.FALSE;
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
	private static BooleanMatch doesAnyItemInThisListMatch(final ListType theList, final String name) {
		if (theList.has(name)) {
			ParserInternalTypeBase pb = theList.get(name);
			return internalMatchType(pb, name);
		}
		return BooleanMatch.FALSE;
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
	private BooleanMatch doesAnyItemInMyListMatch(final String name) {
		return doesAnyItemInThisListMatch((ListType) this.thisItem, name);
	}
}
