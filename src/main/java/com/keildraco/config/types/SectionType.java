package com.keildraco.config.types;

import javax.annotation.Nullable;

public class SectionType extends ParserInternalTypeBase {

	public SectionType(final String name) {
		this(null, name);
	}

	public SectionType(@Nullable final ParserInternalTypeBase parent, final String name) {
		super(parent, name);
		if (this.getName().equals("") || parent == null) {
			this.setName("ROOT");
		}
	}

	public SectionType(@Nullable final ParserInternalTypeBase parent, final String name, final String value) {
		this(parent, name);
	}

	@Override
	public ItemType getType() {
		return ItemType.SECTION;
	}

	@Override
	public String asString() {
		final StringBuilder k = new StringBuilder();

		if (!this.getName().equals("ROOT")) {
			k.append(String.format("%s {%n", this.getName()));
		}

		this.items.values().stream()
		.forEach(v -> k.append(String.format(" %s%n", v.asString())));

		if (!this.getName().equals("ROOT")) {
			k.append(String.format("}%n"));
		}

		return k.toString();
	}
}
