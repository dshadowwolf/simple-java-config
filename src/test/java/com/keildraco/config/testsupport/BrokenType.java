/**
 * 
 */
package com.keildraco.config.testsupport;

import com.keildraco.config.interfaces.ParserInternalTypeBase;


/**
 * @author SYSTEM
 *
 */
public class BrokenType extends ParserInternalTypeBase {

	public BrokenType(final String name) {
		super(name);
		throw new IllegalAccessError("blargh");
	}
	
	/* (non-Javadoc)
	 * @see com.keildraco.config.interfaces.ParserInternalTypeBase#getValue()
	 */
	@Override
	public String getValue() {
		return "";
	}

	/* (non-Javadoc)
	 * @see com.keildraco.config.interfaces.ParserInternalTypeBase#getValueRaw()
	 */
	@Override
	public String getValueRaw() {
		return "";
	}

}
