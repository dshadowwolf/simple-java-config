package com.keildraco.config.types;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ParserInternalTypeBase {
	private final ParserInternalTypeBase parent;
	private String name;
	private Map<String,ParserInternalTypeBase> items;
	public static final ParserInternalTypeBase EmptyType = new ParserInternalTypeBase("EMPTY") {
		@Override
		public boolean has(String itemName) { return false; }
		@Override
		public ParserInternalTypeBase get(String itemName) { return null; }
		@Override
		public void addItem(ParserInternalTypeBase item) { return; }
		@Override
	    public ItemType getType() { return ItemType.EMPTY; }
	};
	
	public ParserInternalTypeBase() {
		this(null, "");
	}
	
	public ParserInternalTypeBase(String name) {
		this(null, name);
	}
	
	public ParserInternalTypeBase(ParserInternalTypeBase parent, String name) {
		this.name = name;
		this.parent = parent;
		this.items = new ConcurrentHashMap<>();
	}
	
	public ParserInternalTypeBase(ParserInternalTypeBase parent, String name, String value) {
		this(parent, name);
	}
	
    public ParserInternalTypeBase get(String itemName)  {
    	if(this.has(itemName)) return this.items.get(itemName);
    	else return ParserInternalTypeBase.EmptyType;
    }
    
    public boolean has(String itemName) { return this.items.containsKey(itemName); } 
    
    public enum ItemType {
        SECTION, IDENTIFIER, NUMBER, BOOLEAN, LIST, OPERATION, INVALID, EMPTY;
    };
    
    public ItemType getType() { return ItemType.INVALID; }
    
    public String asString() {
    	return "BaseType()";
    }
    
    public Number toNumber()  {
    	return Float.NaN;
    }
    
    public boolean toBoolean()  {
    	return Boolean.FALSE;
    }
    
    public List<ParserInternalTypeBase> toList() {
    	return Collections.emptyList();
    }
    
    public void setName(String name) {
    	this.name = name;
    }
    
    public String getName() {
    	return this.name;
    }
    
    public void addItem(ParserInternalTypeBase item) {
    	this.items.put(item.getName(),item);
    }
    
    public ParserInternalTypeBase getParent() {
    	return this.parent!=null?this.parent:EmptyType;
    }
    
}
