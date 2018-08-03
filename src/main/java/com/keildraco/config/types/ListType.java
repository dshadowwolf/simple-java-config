package com.keildraco.config.types;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Nullable;

import com.keildraco.config.interfaces.ParserInternalTypeBase;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class ListType extends ParserInternalTypeBase {

	/**
	 *
	 */
	private final List<ParserInternalTypeBase> value;

	/**
	 *
	 * @param n
	 */
	public ListType(final String n) {
		this(n, Collections.emptyList());
	}

	/**
	 *
	 * @param n
	 * @param values
	 */
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

	/**
	 *
	 * @param parent
	 * @param name
	 */
	public ListType(@Nullable final ParserInternalTypeBase parent, final String name) {
		super(parent, name);
		this.value = new LinkedList<>();
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param valueIn
	 */
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
			return EMPTY_TYPE;
		}

		return this.value.stream().filter(pitb -> pitb.getName().equalsIgnoreCase(s))
				.collect(Collectors.toList()).get(0);
	}

	/**
	 *
	 * @return
	 */
	public List<ParserInternalTypeBase> getValueAsList() {
		return Collections.unmodifiableList(this.value);
	}

	@Override
	public ItemType getType() {
		return ItemType.LIST;
	}

	/**
	 *
	 * @param item
	 * @return
	 */
	private String getItemValue(final ParserInternalTypeBase item) {
		return item.getValue();
	}

	@Override
	public String getValue() {
		final String format = this.getValueRaw();
		if (this.getName().equals("")) {
			return format;
		}
		return String.format("%s = %s", this.getName(), format);
	}

	@Override
	public String getValueRaw() {
		return String.format("[ %s ]",
				this.value.stream().map(this::getItemValue).collect(Collectors.joining(", ")));
	}
}
