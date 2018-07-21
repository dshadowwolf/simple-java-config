package com.keildraco.config.types;

public class NumberType extends ParserInternalTypeBase {
	private Number value;

	public NumberType(String n, Number v) {
		this(null,n,v);
	}

	public NumberType(ParserInternalTypeBase parent, String name, Number value) {
		super(parent, name);
		this.value = value;
	}

	public NumberType(String n, String v) {
		this(null,n,v);
	}
	
	public NumberType(ParserInternalTypeBase parent, String name, String value) {
		super(parent,name);
		if(value.toLowerCase().startsWith("0x")) {
			this.value = Integer.parseInt(value, 16);
		} else if(value.matches("\\s*o[0-7]+\\s*")) {
			this.value = Integer.parseInt(value.substring(1), 8);
		} else if(value.matches("\\s*[0-9]+\\.[0-9]+\\s*")) {
			this.value = Double.parseDouble(value);
		} else if(value.matches("\\s*[1-9][0-9]*\\s*")) {
			this.value = Integer.parseInt(value);
		} else {
			this.value = Integer.valueOf(0);
		}
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
	public Number toNumber() {
		return this.value;
	}

	@Override
	public String asString() {
		if(this.getName().equals("")) return this.value.toString();
		return String.format("%s = %s", this.getName(), this.value.toString());
	}
	
	@Override
	public ItemType getType() {
		return ItemType.NUMBER;
	}
}