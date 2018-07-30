package com.keildraco.config.types;


public class OperationType extends ParserInternalTypeBase {
	private String ident;
	private String operator;
	
	public OperationType() {
		// TODO Auto-generated constructor stub
	}

	public OperationType(String name) {
		super(name);
		System.err.println("new Operation: "+name);
	}

	public OperationType(ParserInternalTypeBase parent, String name) {
		super(parent, name);
		System.err.println("new Operation: "+parent.toString()+", "+name);
	}

	public OperationType(ParserInternalTypeBase parent, String name, String value) {
		super(parent, name);
		this.ident = value;
		if(parent!=null)
			System.err.println("new Operation: "+parent.toString()+", "+name+", "+value);
		else
			System.err.println("new Operation: (null), "+name+", "+value);
	}

	public OperationType setOperation(String oper) {
		System.err.println("set operator for "+this.getName()+" :: "+ this.ident+" to \""+oper+"\"");
		this.operator = oper.trim();
		return this;
	}
	
	public int getOperator() {
		return this.operator.charAt(0);
	}
	
	@Override
	public String asString() {
		return String.format("%s(%s %s)", this.getName(), this.operator, this.ident);
	}
	
	@Override
	public ItemType getType() {
		return ItemType.OPERATION;
	}
	
	@Override
	public String getValue() {
		return String.format("%s", this.ident); // force a copy, period
	}
}
