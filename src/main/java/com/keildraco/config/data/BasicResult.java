package com.keildraco.config.data;

import java.util.stream.Collectors;

import com.keildraco.config.interfaces.ParserInternalTypeBase;

public final class BasicResult extends ParserInternalTypeBase {

	public BasicResult(final String name) {
		super(name);
	}

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
