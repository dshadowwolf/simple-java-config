package com.keildraco.config.types;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.keildraco.config.Config;

public class ParserInternalTypeBase {
	private final ParserInternalTypeBase parent;
	private String name;
	protected Map<String,ParserInternalTypeBase> items;
	public static final ParserInternalTypeBase EmptyType = new ParserInternalTypeBase("EMPTY") {
		@Override
		public boolean has(String itemName) { return false; }
		@Override
		public ParserInternalTypeBase get(String itemName) { return null; }
		@Override
		public void addItem(ParserInternalTypeBase item) { /* the EmptyType does not store other items */ }
		@Override
	    public ItemType getType() { return ItemType.EMPTY; }
	};
	
	public ParserInternalTypeBase(String name) {
		this(null, name);
	}
	
	public ParserInternalTypeBase(@Nullable ParserInternalTypeBase parent, String name) {
		this.name = name;
		this.parent = parent;
		this.items = new ConcurrentHashMap<>();
	}
	
	public ParserInternalTypeBase(@Nullable ParserInternalTypeBase parent, String name, String value) {
		this(parent, name);
	}
	
    public ParserInternalTypeBase get(String itemName)  {
    	if(itemName.indexOf('.') > 0) {
    		String nameBits = itemName.substring(0,itemName.indexOf('.'));
    		if(this.has(nameBits)) {
    			String nameRest = itemName.substring(itemName.indexOf('.')+1);
    			return this.get(nameBits)!=null?this.get(nameBits).get(nameRest):EmptyType;
    		}
    	} else if(this.has(itemName)) {
    		return this.items.get(itemName);
    	}
    	return ParserInternalTypeBase.EmptyType;
    }
    
    public boolean has(String itemName) {
    	if(itemName.contains(".")) {
    		String nn = itemName.substring(0, itemName.indexOf('.'));
    		String rest = itemName.substring(itemName.indexOf('.')+1);
    		boolean a = this.items.containsKey(nn);
    		boolean b = this.items.getOrDefault(nn,EmptyType).has(rest);
    		return a&&b;
    	}

    	return this.items.containsKey(itemName);
    } 
    
    public enum ItemType {
        SECTION, IDENTIFIER, NUMBER, BOOLEAN, LIST, OPERATION, INVALID, EMPTY;
    }
    
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
    
    public Map<String, ParserInternalTypeBase> getChildren() {
    	return Collections.unmodifiableMap(this.items);
    }
    
    public ParserInternalTypeBase getParent() {
    	return this.parent!=null?this.parent:EmptyType;
    }
    
    public String getValue() {
    	return "";
    }
    
}
