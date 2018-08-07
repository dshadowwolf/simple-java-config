package com.keildraco.config.interfaces;

import com.keildraco.config.Config;
import com.keildraco.config.data.Constants;

public final class EmptyParserType extends ParserInternalTypeBase {

	/**
	 *
	 */
	public EmptyParserType() {
		super(null, Constants.EMPTY_TYPE_NAME, Constants.EMPTY_TYPE_VALUE);
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
		return Constants.EMPTY_TYPE_VALUE;
	}

	/**
	 *
	 * @return
	 */
	@Override
	public String getValueRaw() {
		return Constants.EMPTY_TYPE_VALUE;
	}
}
