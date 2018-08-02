package com.keildraco.config.data;

import com.keildraco.config.interfaces.ParserInternalTypeBase;

public final class DataQuery {

	private ParserInternalTypeBase baseSection;

	private DataQuery(final ParserInternalTypeBase section) {
		this.baseSection = section;
	}

	// as odd as it seems, this cannot, legally, be called with anything other than a valid,
	// non-null SectionType value - not even 'EmptyType' can be used here and be valid or compile
	public static DataQuery of(final ParserInternalTypeBase section) {
		return new DataQuery(section);
	}

	/**
	 *
	 * @param key
	 *            Name of item to look for, can be a dotted-notation for sub-items
	 * @return true/false of items existence
	 */
	public boolean get(final String key) {
		// find item, or "all"
		if (this.baseSection.has(key)) {
			return true;
		} else if (key.indexOf('.') > 0) {
			final String base = String.format("%s.all", key.substring(0, key.lastIndexOf('.')));
			if (this.baseSection.has(base)) {
				final String term = key.substring(key.lastIndexOf('.') + 1);
				return new ItemMatcher(this.baseSection.get(base)).matches(term);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
