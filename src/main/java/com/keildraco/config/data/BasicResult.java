package com.keildraco.config.data;

import java.util.stream.Collectors;

import com.keildraco.config.interfaces.ParserInternalTypeBase;
import static com.keildraco.config.data.Constants.NEWLINE_FORMAT_STRING;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class BasicResult extends ParserInternalTypeBase {

	/**
	 * Standard Constructor. This has to exist, none of the rest of them do (for this type, at least)
	 * @param name Name to give this.
	 */
	public BasicResult(final String name) {
		super(name);
	}

	/**
	 * Internal helper for getting things done in an efficient manner in {@link #getValue() getValue()}.
	 * @param value The {@link ParserInternalTypeBase} value that we need to convert to a String.
	 * @return The result of calling {@link ParserInternalTypeBase#getValue() value.getValue()}.
	 */
	private static String valAsString(final ParserInternalTypeBase value) {
		return value.getValue();
	}

	/**
	 * Override of {@link ParserInternalTypeBase#getValue()} so that it returns all the values it contains, as formatted by their own getValue() overrides, separated by System specific line-endings.
	 */
	@Override
	public String getValue() {
		return String.join(String.format(NEWLINE_FORMAT_STRING), this.getItems().values().stream()
				.map(BasicResult::valAsString).collect(Collectors.toList()));
	}

	
	/**
	 * Override of {@link ParserInternalTypeBase#getValueRaw()} that is just a wrapper around {@link #getValue()} as the BasicResult does not have a single value that can be represented in any other manner.
	 */
	@Override
	public String getValueRaw() {
		return this.getValue();
	}
}
