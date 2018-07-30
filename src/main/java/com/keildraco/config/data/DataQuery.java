package com.keildraco.config.data;

import com.keildraco.config.Config;
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
		Config.LOGGER.fatal("looking for key %s", key);
		// find item, or "all"
		if(this.baseSection.has(key)) {
			Config.LOGGER.fatal("found %s", key);
			return true;
		} else if(key.indexOf('.') > 0) {
			String base = String.format("%s.all", key.substring(0, key.lastIndexOf('.')));
			if(this.baseSection.has(base)) {
				Config.LOGGER.fatal("found \"all\", doing an \"all\" check");
				String term = key.substring(key.lastIndexOf('.')+1);
				Config.LOGGER.fatal("checking if \"all\" matches \"%s\"", term);
				return new ItemMatcher(this.baseSection.get(base)).matches(term);
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
