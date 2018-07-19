package com.keildraco.config.types;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SectionType extends ParserInternalTypeBase {
	private Map<String,ParserInternalTypeBase> values;
	
	public SectionType(String name) {
		this(null,name);
	}
	
	public SectionType(ParserInternalTypeBase parent, String name) {
		super(parent,name);
		this.values = new ConcurrentHashMap<>();
	}
	
	public SectionType(ParserInternalTypeBase parent, String name, String value) {
		this(parent,name);
	}
	
	@Override
	public void addItem(ParserInternalTypeBase item) {
		this.values.put(item.getName(),item);
	}
	
	@Override
	public boolean has(String s) {
		return this.values.containsKey(s);
	}
	
	@Override
	public ParserInternalTypeBase get(String s) {
		return this.values.getOrDefault(s, ParserInternalTypeBase.EmptyType);
	}

	@Override
	public ItemType getType() {
		return ItemType.SECTION;
	}
	
	@Override
	public String asString() {
		StringBuilder k = new StringBuilder();
		k.append(String.format("%s {\n", this.getName()));
		this.values.entrySet().stream()
		.map( ent -> String.format("\t%s\n", ent.getValue().asString()) ).forEach( k::append );
		return k.toString();
	}
}
