package com.keildraco.config.data;

public final class BooleanMatch {
	public static enum VALS {
		FALSE_I,
		TRUE_I,
		FORCED_T,
		FORCED_F
	};
	
	public static final BooleanMatch TRUE = new BooleanMatch(VALS.TRUE_I);
	public static final BooleanMatch FALSE = new BooleanMatch(VALS.FALSE_I);
	public static final BooleanMatch FORCED_TRUE = new BooleanMatch(VALS.FORCED_T);
	public static final BooleanMatch FORCED_FALSE = new BooleanMatch(VALS.FORCED_F);
	
	private final VALS value;
	
	public BooleanMatch(VALS v) {
		this.value = v;
	}
	
	public boolean getValue() {
		return (this.value == VALS.TRUE_I||this.value == VALS.FORCED_T)?true:false;
	}
}
