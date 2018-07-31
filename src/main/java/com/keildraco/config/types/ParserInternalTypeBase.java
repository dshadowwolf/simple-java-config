package com.keildraco.config.types;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public class ParserInternalTypeBase {
	private final ParserInternalTypeBase parent;
	private String name;
	protected final Map<String,ParserInternalTypeBase> items;
	public static final ParserInternalTypeBase EmptyType = new ParserInternalTypeBase("EMPTY") {
		@Override
		public boolean has(final String itemName) { return false; }

		@Override
		public ParserInternalTypeBase get(final String itemName) { return null; }

		@Override
		public void addItem(final ParserInternalTypeBase item) { /* the EmptyType does not store other items */ }

		@Override
	    public ItemType getType() { return ItemType.EMPTY; }
	};

	public ParserInternalTypeBase(final String name) {
		this(null, name);
	}

	public ParserInternalTypeBase(@Nullable final ParserInternalTypeBase parent, final String name) {
		this.name = name;
		this.parent = parent;
		this.items = new ConcurrentHashMap<>();
	}

	public ParserInternalTypeBase(@Nullable final ParserInternalTypeBase parent, final String name, final String value) {
		this(parent, name);
	}

    public ParserInternalTypeBase get(final String itemName)  {
    	if (itemName.indexOf('.') > 0) {
    		final String nameBits = itemName.substring(0,itemName.indexOf('.'));
    		if (this.has(nameBits)) {
    			final String nameRest = itemName.substring(itemName.indexOf('.')+1);
    			return this.get(nameBits)!=null?this.get(nameBits).get(nameRest):EmptyType;
    		}
    	} else if (this.has(itemName)) {
    		return this.items.get(itemName);
    	}
    	return ParserInternalTypeBase.EmptyType;
    }

    public boolean has(final String itemName) {
    	if (itemName.contains(".")) {
    		final String nn = itemName.substring(0, itemName.indexOf('.'));
    		final String rest = itemName.substring(itemName.indexOf('.')+1);
    		final boolean a = this.items.containsKey(nn);
    		final boolean b = this.items.getOrDefault(nn,EmptyType).has(rest);
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

    public void setName(final String name) {
    	this.name = name;
    }

    public String getName() {
    	return this.name;
    }

    public void addItem(final ParserInternalTypeBase item) {
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
