package com.keildraco.config.data;

import com.keildraco.config.types.*;

public class DataQuery {
	private SectionType baseSection;

	private DataQuery(final SectionType section) {
		this.baseSection = section;
	}

	// as odd as it seems, this cannot, legally, be called with anything other than a valid,
	// non-null SectionType value - not even 'EmptyType' can be used here and be valid or compile
	public static DataQuery of(final SectionType section) {
		return new DataQuery(section);
	}

	public boolean get(final String key) {
		// find item, or "all"
		if (this.baseSection.has(key)) {
			return true;
		} else if (key.indexOf('.') > 0) {
			final String base = String.format("%s.all", key.substring(0, key.lastIndexOf('.')));
			if (this.baseSection.has(base)) {
				final String term = key.substring(key.lastIndexOf('.')+1);
				return new ItemMatcher(this.baseSection.get(base)).matches(term);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
