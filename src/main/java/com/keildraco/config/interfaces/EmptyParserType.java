package com.keildraco.config.interfaces;

import com.keildraco.config.Config;

public final class EmptyParserType extends ParserInternalTypeBase {

	private static final String MY_VALUE = "EMPTY";

	/**
	 *
	 */
	public EmptyParserType() {
		super(null, MY_VALUE, MY_VALUE);
	}

	/**
	 *
	 * @param itemName
	 * @return
	 */
	@Override
	public boolean has(final String itemName) {
		return false;
	}

	/**
	 *
	 * @param itemName
	 * @return
	 */
	@Override
	public ParserInternalTypeBase get(final String itemName) {
		return Config.EMPTY_TYPE;
	}

	/**
	 *
	 * @param item
	 */
	@Override
	public void addItem(final ParserInternalTypeBase item) {
		/* the EmptyType does not store other items */
	}

	/**
	 *
	 * @return
	 */
	@Override
	public ItemType getType() {
		return ItemType.EMPTY;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getValue() {
		return MY_VALUE;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getValueRaw() {
		return this.getValue();
	}
}
