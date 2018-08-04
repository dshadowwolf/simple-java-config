package com.keildraco.config.interfaces;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
public interface IStateParser {

	/**
	 *
	 * @param factory
	 */
	void setFactory(TypeFactory factory);

	/**
	 *
	 * @return
	 */
	TypeFactory getFactory();

	/**
	 *
	 * @param tok
	 * @return
	 */
	ParserInternalTypeBase getState(Tokenizer tok);

	/**
	 *
	 * @param parent
	 */
	void setParent(ParserInternalTypeBase parent);

	/**
	 *
	 * @return
	 */
	ParserInternalTypeBase getParent();

	/**
	 *
	 * @param name
	 */
	void setName(String name);

	/**
	 *
	 * @return
	 */
	String getName();

	/**
	 *
	 * @param factory
	 */
	void registerTransitions(TypeFactory factory);
}
