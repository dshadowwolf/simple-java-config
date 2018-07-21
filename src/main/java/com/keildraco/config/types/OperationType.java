package com.keildraco.config.types;


public class OperationType extends ParserInternalTypeBase {
	private String ident;
	private String operator;
	
	public OperationType() {
		// TODO Auto-generated constructor stub
	}

	public OperationType(String name) {
		super(name);
	}

	public OperationType(ParserInternalTypeBase parent, String name) {
		super(parent, name);
	}

	public OperationType(ParserInternalTypeBase parent, String name, String value) {
		super(parent, name);
		this.ident = value;
	}

	public OperationType setOperation(String oper) {
		this.operator = oper;
		return this;
	}
	
	@Override
	public String asString() {
		return String.format("%s(%s %s)", this.getName(), this.operator, this.ident);
	}
	
	@Override
	public ItemType getType() {
		return ItemType.OPERATION;
	}
}
