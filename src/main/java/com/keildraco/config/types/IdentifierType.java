package com.keildraco.config.types;

import javax.annotation.Nullable;

public class IdentifierType extends ParserInternalTypeBase {
	private String ident;
	
	public IdentifierType(String n) {
		this(n, n);
	}
	
	public IdentifierType(String n, String v) {
		this(null,n,v);
	}
	
	
	public IdentifierType(@Nullable ParserInternalTypeBase parent, String name, String value) {
		super(parent, name);
		this.ident = value;
	}
	
	@Override
	public void addItem(ParserInternalTypeBase item) {
		// identifiers - that is, key-value pairs - can't store more than an identifier they equal, so this method gets stubbed
	}
	
	@Override
	public boolean has(String s) {
		return this.getName().equalsIgnoreCase(s)||this.ident.equalsIgnoreCase(s);
	}
	
	@Override
	public ParserInternalTypeBase get(String s) {
		return this.has(s)?this:EmptyType;
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
	
	@Override
	public String getValue() {
		return this.ident;
	}
}
