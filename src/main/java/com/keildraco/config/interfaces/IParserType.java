package com.keildraco.config.interfaces;

import javax.annotation.Nullable;

/**
 *
 * @author Daniel Hazelton
 *
 */
public interface IParserType {

	/**
	 *
	 * @param parent
	 * @param name
	 * @param value
	 * @return
	 */
	ParserInternalTypeBase get(@Nullable ParserInternalTypeBase parent, String name, String value);
}
