package com.keildraco.config.interfaces;

import java.util.Locale;

import javax.annotation.Nullable;

import com.keildraco.config.factory.TypeFactory;

/**
 *
 * @author Daniel Hazelton
 *
 */
public abstract class AbstractParserBase implements IStateParser {

	/**
	 *
	 */
	private TypeFactory factory;

	/**
	 *
	 */
	@Nullable
	private ParserInternalTypeBase parent;

	/**
	 *
	 */
	private String name;

	/**
	 *
	 * @param factoryIn
	 * @param parentIn
	 * @param nameIn
	 */
	public AbstractParserBase(final TypeFactory factoryIn,
			@Nullable final ParserInternalTypeBase parentIn, final String nameIn) {
		this.factory = factoryIn;
		this.parent = parentIn;
		this.name = nameIn.toUpperCase(Locale.getDefault());
	}

	/**
	 *
	 */
	@Override
	public void setFactory(final TypeFactory factoryIn) {
		this.factory = factoryIn;
	}

	/**
	 *
	 */
	@Override
	public TypeFactory getFactory() {
		return this.factory;
	}

	/**
	 *
	 */
	@Override
	public void setParent(final ParserInternalTypeBase parentIn) {
		this.parent = parentIn;
	}

	/**
	 *
	 */
	@Override
	public ParserInternalTypeBase getParent() {
		return this.parent;
	}

	/**
	 *
	 */
	@Override
	public String getName() {
		return this.name;
	}

	/**
	 *
	 */
	@Override
	public void setName(final String nameIn) {
		this.name = nameIn;
	}
}
