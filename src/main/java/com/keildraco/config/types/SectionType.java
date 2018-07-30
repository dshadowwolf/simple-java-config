package com.keildraco.config.types;

import javax.annotation.Nullable;

public class SectionType extends ParserInternalTypeBase {
	public SectionType(String name) {
		this(null,name);
	}
	
	public SectionType(@Nullable ParserInternalTypeBase parent, String name) {
		super(parent,name);
		if(this.getName().equals("")||parent==null) this.setName("ROOT");
	}
	
	public SectionType(@Nullable ParserInternalTypeBase parent, String name, String value) {
		this(parent,name);
	}
	
	@Override
	public ItemType getType() {
		return ItemType.SECTION;
	}
	
	@Override
	public String asString() {
		StringBuilder k = new StringBuilder();

		if(!this.getName().equals("ROOT")) {
			k.append(String.format("%s {%n", this.getName()));
		}
		
		this.items.values().stream()
		.forEach( v -> k.append(String.format(" %s%n", v.asString() )));

		if(!this.getName().equals("ROOT")) {
			k.append("}%n");
		}
		
		return k.toString();
	}	
}
