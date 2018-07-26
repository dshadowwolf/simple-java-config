package com.keildraco.config.data;

import com.keildraco.config.types.*;
import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

public class DataQuery {
	private SectionType baseSection;
	private ItemMatcher matcher;
	private DataQuery() {
		throw new IllegalAccessError("Cannot instantiate with no parameters");
	}
	
	private DataQuery(SectionType section) {
		this.baseSection = section;
	}

	public static DataQuery of(SectionType section) {
		return new DataQuery(section);
	}
	
	public boolean get(String key) {
		return this.matcher==null?false:this.matcher.matches(key);
	}
	
	/**
	 * Walk the parse-tree and convert it to a queryable format
	 */
	public DataQuery create() {
		this.matcher = new ItemMatcher(this.baseSection);
		return this;
	}
}
