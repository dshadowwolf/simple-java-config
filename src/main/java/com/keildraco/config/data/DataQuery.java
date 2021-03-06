package com.keildraco.config.data;

import com.keildraco.config.interfaces.ParserInternalTypeBase;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class DataQuery {

	/**
	 *
	 */
	private final ParserInternalTypeBase baseSection;

	/**
	 *
	 * @param section
	 */
	private DataQuery(final ParserInternalTypeBase section) {
		this.baseSection = section;
	}

	/**
	 * as odd as it seems, this cannot, legally, be called with anything other than a valid,
	 * non-null SectionType value - not even 'EmptyType' can be used here and be valid or compile.
	 *
	 * @param section
	 * @return
	 */
	public static DataQuery of(final ParserInternalTypeBase section) {
		return new DataQuery(section);
	}

	/**
	 *
	 * @param key
	 *            Name of item to look for, can be a dotted-notation for sub-items
	 * @return true/false of items existence
	 */
	public boolean matches(final String key) {
		// find item, or "all"
		final int index = key.indexOf('.');
		if ((index != -1) && (index > 0)) {
			return new ItemMatcher(this.baseSection).matches(key);
		} else if (index == 0) {
			throw new IllegalArgumentException("search keys must not start with '.'");
		} else {
			return this.baseSection.has(key);
		}
	}
}
