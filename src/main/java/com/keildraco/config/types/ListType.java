package com.keildraco.config.types;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.keildraco.config.interfaces.ParserInternalTypeBase;

public final class ListType extends ParserInternalTypeBase {

	private final List<ParserInternalTypeBase> value;

	public ListType(final String n) {
		this(n, Collections.emptyList());
	}

	public ListType(final String n, final List<ParserInternalTypeBase> values) {
		this(null, n, values);
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param values
	 */
	public ListType(@Nullable final ParserInternalTypeBase parent, final String name,
			final List<ParserInternalTypeBase> values) {
		super(parent, name);
		this.value = new LinkedList<>();
		this.value.addAll(values);
	}

	public ListType(@Nullable final ParserInternalTypeBase parent, final String name) {
		super(parent, name);
		this.value = new LinkedList<>();
	}

	public ListType(@Nullable final ParserInternalTypeBase parent, final String name,
			final String valueIn) {
		this(parent, name);
	}

	@Override
	public void addItem(final ParserInternalTypeBase item) {
		this.value.add(item);
	}

	@Override
	public boolean has(final String s) {
		return this.value.stream().anyMatch(pitb -> pitb.getName().equalsIgnoreCase(s));
	}

	@Override
	public ParserInternalTypeBase get(final String s) {
		if (!this.has(s)) {
			return EmptyType;
		}

		return this.value.stream().filter(pitb -> pitb.getName().equalsIgnoreCase(s)).findFirst()
				.get();
	}

	@Override
	public List<ParserInternalTypeBase> toList() {
		return Collections.unmodifiableList(this.value);
	}

	@Override
	public ItemType getType() {
		return ItemType.LIST;
	}

	@Override
	public String asString() {
		final String format = String.format("[ %s ]",
				this.value.stream()
						.map(v -> v.getType() == ItemType.OPERATION ? v.asString() : v.getValue())
						.collect(Collectors.joining(", ")));

		if (this.getName().equals("")) {
			return format;
		}
		return String.format("%s = %s", this.getName(), format);
	}
}
