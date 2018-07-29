package com.keildraco.config.types;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class ListType extends ParserInternalTypeBase {
	private List<ParserInternalTypeBase> value;
	
	public ListType(String n) {
		this(n, Collections.emptyList());
	}

	public ListType(String n, List<ParserInternalTypeBase> value) {
		this(null, n, value);
	}
	
	public ListType(ParserInternalTypeBase parent, String name, List<ParserInternalTypeBase> value) {
		super(parent, name);
		this.value = new LinkedList<>();
		this.value.addAll(value);
	}
	
	public ListType(ParserInternalTypeBase parent, String name) {
		super(parent,name);
		this.value = new LinkedList<>();
	}
	
	public ListType(ParserInternalTypeBase parent, String name, String value) {
		this(parent, name);
	}
	
	@Override
	public void addItem(ParserInternalTypeBase item) {
		this.value.add(item);
	}
	
	@Override
	public boolean has(String s) {
		return this.value.stream().filter(pitb -> pitb.getName().equalsIgnoreCase(s)).findFirst().isPresent();
	}
	
	@Override
	public ParserInternalTypeBase get(String s) {
		Optional<ParserInternalTypeBase> rv = this.value.stream().filter(pitb -> pitb.getName().equalsIgnoreCase(s)).findFirst();
		if(rv.isPresent()) return rv.get();
		else return EmptyType;
	}
	
	@Override
	public List<ParserInternalTypeBase> toList() {
		return Collections.unmodifiableList(this.value);
	}

	@Override
	public ItemType getType() {
		return ItemType.LIST;
	}

	@Override
	public String asString() {
		if(this.getName().equals("")) return String.format("[ %s ]", this.value.stream().map(v -> v.asString()).collect(Collectors.joining(", ")));
		
		return String.format("%s = [ %s ]", this.getName(), this.value.stream().map(v -> v.asString()).collect(Collectors.joining(", ")));
	}
}
