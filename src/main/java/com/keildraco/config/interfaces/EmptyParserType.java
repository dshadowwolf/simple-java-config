package com.keildraco.config.interfaces;

import javax.annotation.Nullable;

import com.keildraco.config.Config;

public final class EmptyParserType extends ParserInternalTypeBase {
	private static final String MY_VALUE = "EMPTY";
	
	public EmptyParserType(final String nameIn) {
		super(null, nameIn, MY_VALUE);
	}
	
	public EmptyParserType(@Nullable final ParserInternalTypeBase parentIn,
			final String nameIn) {
		super(parentIn, nameIn, MY_VALUE);
	}
	
	public EmptyParserType(@Nullable final ParserInternalTypeBase parentIn,
			final String nameIn, final String valueIn) {
		super(parentIn, nameIn, valueIn);
	}
	
	@Override
	public boolean has(final String itemName) {
		return false;
	}

	@Override
	public ParserInternalTypeBase get(final String itemName) {
		return Config.EMPTY_TYPE;
	}

	@Override
	public void addItem(final ParserInternalTypeBase item) {
		/* the EmptyType does not store other items */
	}

	@Override
	public ItemType getType() {
		return ItemType.EMPTY;
	}

	@Override
	public String getValue() {
		return MY_VALUE;
	}

	@Override
	public String getValueRaw() {
		return this.getValue();
	}
}
