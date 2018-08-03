package com.keildraco.config.data;

import java.util.stream.Collectors;

import com.keildraco.config.interfaces.ParserInternalTypeBase;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class BasicResult extends ParserInternalTypeBase {

	/**
	 *
	 * @param name
	 */
	public BasicResult(final String name) {
		super(name);
	}

	/**
	 *
	 * @param val
	 * @return
	 */
	private String valAsString(final ParserInternalTypeBase val) {
		return val.getValue();
	}

	@Override
	public String getValue() {
		return String.join(String.format("%n"),
				this.items.values().stream().map(this::valAsString).collect(Collectors.toList()));
	}

	@Override
	public String getValueRaw() {
		return this.getValue();
	}
}
