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
	private static List<String> specialNames = Arrays.asList("all");
	private static Map<String, List<String>> aggregates = new ConcurrentHashMap<>();
	private List<String> pieces;
	private SectionType baseSection;
	
	private DataQuery() {
		throw new IllegalAccessError("Cannot instantiate with no parameters");
	}
	
	private DataQuery(SectionType section) {
		this.baseSection = section;
		this.pieces = new LinkedList<>();
	}

	public static DataQuery of(SectionType section) {
		return new DataQuery(section);
	}
	
	public static void addAggregate(String aggregateName, String itemName) {
		List<String> existing = aggregates.getOrDefault(aggregateName, new LinkedList<>());
		existing.add(itemName);
		aggregates.put(aggregateName, existing);
	}
	
	public boolean get(String key) {
		return this.pieces.contains(key);
	}
	
	/**
	 * Walk the parse-tree and convert it to a queryable format
	 */
	public DataQuery create() {
		List<String> raw = this.baseSection.flattenData();
		
		return null;
	}
}
