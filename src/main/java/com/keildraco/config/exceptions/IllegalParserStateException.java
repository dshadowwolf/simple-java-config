package com.keildraco.config.exceptions;

public class IllegalParserStateException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3178889090285085141L;

	public IllegalParserStateException() {
		super("Illegal State Encountered During Parse");
	}

	public IllegalParserStateException(String arg0) {
		super(arg0);
	}

	public IllegalParserStateException(Throwable arg0) {
		super(arg0);
	}

	public IllegalParserStateException(String arg0, Throwable arg1) {
		super(arg0, arg1);
	}

	public IllegalParserStateException(String arg0, Throwable arg1, boolean arg2, boolean arg3) {
		super(arg0, arg1, arg2, arg3);
	}

}
