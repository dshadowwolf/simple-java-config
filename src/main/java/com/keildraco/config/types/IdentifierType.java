package com.keildraco.config.types;

public class IdentifierType extends ParserInternalTypeBase {
	private String ident;
	
	public IdentifierType(String n) {
		this(n, n);
	}
	
	public IdentifierType(String n, String v) {
		this(null,n,v);
	}
	
	
	public IdentifierType(ParserInternalTypeBase parent, String name, String value) {
		super(parent, name);
		this.ident = value;
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
	public String asString() {
		if(this.getName().equals("")) return this.ident;
		return String.format("%s = %s", this.getName(), this.ident);
	}
	
	@Override
	public ItemType getType() {
		return ItemType.IDENTIFIER;
	}
}
