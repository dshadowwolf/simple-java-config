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
	 * @param value
	 * @return
	 */
	private static String valAsString(final ParserInternalTypeBase value) {
		return value.getValue();
	}

	@Override
	public String getValue() {
		return String.join(String.format("%n"), this.getItems().values().stream()
				.map(BasicResult::valAsString).collect(Collectors.toList()));
	}

	@Override
	public String getValueRaw() {
		return this.getValue();
	}
}
