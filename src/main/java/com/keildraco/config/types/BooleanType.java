package com.keildraco.config.types;

public class BooleanType extends ParserInternalTypeBase {
	private Boolean value;
	
	public BooleanType(String n, Boolean v) {
		this(null,n,v);
	}

	public BooleanType(ParserInternalTypeBase parent, String name, Boolean value) {
		super(parent, name);
		this.value = value;
	}
	
	public BooleanType(ParserInternalTypeBase parent, String name, String value) {
		this(parent,name,Boolean.parseBoolean(value));
	}
	
	@Override
	public void addItem(ParserInternalTypeBase item) {
		return;
	}
	
	@Override
	public boolean has(String s) {
		return false;
	}
	
	@Override
	public ParserInternalTypeBase get(String s) {
		return ParserInternalTypeBase.EmptyType;
	}
	
	@Override
	public boolean toBoolean() {
		return this.value;
	}

	@Override
	public String asString() {
		if(this.getName().equals("")) return this.value.toString();
		return String.format("%s = %s", this.getName(), this.value);
	}
	
	@Override
	public ItemType getType() {
		return ItemType.BOOLEAN;
	}
}
