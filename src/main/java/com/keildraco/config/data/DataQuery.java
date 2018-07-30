package com.keildraco.config.data;

import com.keildraco.config.types.*;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

public class DataQuery {
	private SectionType baseSection;
	
	private DataQuery() {
		throw new IllegalAccessError("Cannot instantiate with no parameters");
	}
	
	private DataQuery(SectionType section) {
		this.baseSection = section;
	}

	public static DataQuery of(SectionType section) {
		if(!EmptyType.equals(section))
			return new DataQuery(section);
		return null;
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
