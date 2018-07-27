package com.keildraco.config.data;

import com.keildraco.config.types.*;
import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;

import java.util.Arrays;
import java.util.Deque;

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
		if(section != null && !EmptyType.equals(section))
			return new DataQuery(section);
		return null;
	}
	
	public boolean get(String key) {
		// find item, or "all"
		@SuppressWarnings("unchecked")
		Deque<String> bits = (Deque<String>)Arrays.asList(key.split("\\."));
		String top = bits.pop();
		String rest = String.join(".", bits);
		if(this.baseSection.has(top) && this.baseSection.get(top).getType() == ItemType.SECTION) {
			return new DataQuery((SectionType)this.baseSection.get(top)).create().get(rest);
		} else if(this.baseSection.has(top)) {
			return new ItemMatcher(this.baseSection.get(top)).matches(rest);
		} else if(this.baseSection.has("all")) {
			return new ItemMatcher(this.baseSection.get("all")).matches(key);
		} else {
			return false;
		}
	}
	
	/**
	 * Walk the parse-tree and convert it to a queryable format
	 */
	public DataQuery create() {
		return this;
	}
}
