package com.keildraco.config.data;

import com.keildraco.config.types.*;

public class DataQuery {
	private SectionType baseSection;
	
	private DataQuery(SectionType section) {
		this.baseSection = section;
	}

	// as odd as it seems, this cannot, legally, be called with anything other than a valid,
	// non-null SectionType value - not even 'EmptyType' can be used here and be valid or compile
	public static DataQuery of(SectionType section) {
		return new DataQuery(section);
	}
	
	public boolean get(String key) {
		// find item, or "all"
		if(this.baseSection.has(key)) {
			return true;
		} else if(this.baseSection.has("all")) {
			String term = key.substring(key.lastIndexOf('.'));
			return new ItemMatcher(this.baseSection.get("all")).matches(term);
		} else {
			return false;
		}
	}
}
