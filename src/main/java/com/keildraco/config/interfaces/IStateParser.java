package com.keildraco.config.interfaces;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.tokenizer.Tokenizer;

import javax.annotation.Nullable;

/**
 *
 * @author Daniel Hazelton
 *
 */
public interface IStateParser {

	/**
	 *
	 * @param factoryIn
	 */
	void setFactory(TypeFactory factoryIn);

	/**
	 *
	 * @return
	 */
	TypeFactory getFactory();

	/**
	 *
	 * @param tokenizer
	 * @return
	 */
	ParserInternalTypeBase getState(Tokenizer tokenizer);

	/**
	 *
	 * @param parentIn
	 */
	void setParent(ParserInternalTypeBase parentIn);

	/**
	 *
	 * @return
	 */
	@Nullable
	ParserInternalTypeBase getParent();

	/**
	 *
	 * @param nameIn
	 */
	void setName(String nameIn);

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
