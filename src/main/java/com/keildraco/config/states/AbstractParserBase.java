package com.keildraco.config.states;

import javax.annotation.Nullable;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.ParserInternalTypeBase;

public abstract class AbstractParserBase implements IStateParser {

	protected TypeFactory factory;

	protected boolean errored;

	protected ParserInternalTypeBase parent;

	protected String name;

	/**
	 *
	 * @param factoryIn
	 * @param parentIn
	 * @param nameIn
	 */
	public AbstractParserBase(@Nullable final TypeFactory factoryIn,
			@Nullable final ParserInternalTypeBase parentIn, final String nameIn) {
		this.factory = factoryIn;
		this.errored = false;
		this.parent = parentIn;
		this.name = nameIn;
	}

	@Override
	public void setFactory(final TypeFactory factoryIn) {
		this.factory = factoryIn;
	}

	@Override
	public TypeFactory getFactory() {
		return this.factory;
	}

	@Override
	public void setErrored() {
		this.errored = true;
	}

	@Override
	public boolean errored() {
		return this.errored;
	}

	@Override
	public void setParent(final ParserInternalTypeBase parentIn) {
		this.parent = parentIn;
	}

	@Override
	public ParserInternalTypeBase getParent() {
		return this.parent;
	}

	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public void clearErrors() {
		this.errored = false;
	}

	@Override
	public void setName(final String nameIn) {
		this.name = nameIn;
	}
}
