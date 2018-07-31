package com.keildraco.config.types;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public class ParserInternalTypeBase {

	private final ParserInternalTypeBase parent;

	private String name;

	protected final Map<String, ParserInternalTypeBase> items;

	public static final ParserInternalTypeBase EmptyType = new ParserInternalTypeBase("EMPTY") {

		@Override
		public boolean has(final String itemName) {
			return false;
		}

		@Override
		public ParserInternalTypeBase get(final String itemName) {
			return null;
		}

		@Override
		public void addItem(final ParserInternalTypeBase item) {
			/* the EmptyType does not store other items */
		}

		@Override
		public ItemType getType() {
			return ItemType.EMPTY;
		}
	};

	public ParserInternalTypeBase(final String nameIn) {
		this(null, nameIn);
	}

	/**
	 *
	 * @param parentIn
	 * @param nameIn
	 */
	public ParserInternalTypeBase(@Nullable final ParserInternalTypeBase parentIn,
			final String nameIn) {
		this.name = nameIn;
		this.parent = parentIn;
		this.items = new ConcurrentHashMap<>();
	}

	public ParserInternalTypeBase(@Nullable final ParserInternalTypeBase parentIn, final String nameIn,
			@SuppressWarnings("unused") final String valueIn) {
		this(parentIn, nameIn);
	}

	/**
	 *
	 * @param itemName
	 * @return
	 */
	public ParserInternalTypeBase get(final String itemName) {
		if (itemName.indexOf('.') > 0) {
			final String nameBits = itemName.substring(0, itemName.indexOf('.'));
			if (this.has(nameBits)) {
				final String nameRest = itemName.substring(itemName.indexOf('.') + 1);
				return this.get(nameBits) != null ? this.get(nameBits).get(nameRest) : EmptyType;
			}
		} else if (this.has(itemName)) {
			return this.items.get(itemName);
		}
		return ParserInternalTypeBase.EmptyType;
	}

	/**
	 *
	 * @param itemName
	 * @return
	 */
	public boolean has(final String itemName) {
		if (itemName.contains(".")) {
			final String nn = itemName.substring(0, itemName.indexOf('.'));
			final String rest = itemName.substring(itemName.indexOf('.') + 1);
			final boolean a = this.items.containsKey(nn);
			final boolean b = this.items.getOrDefault(nn, EmptyType).has(rest);
			return a && b;
		}

		return this.items.containsKey(itemName);
	}

	public enum ItemType {
		SECTION, IDENTIFIER, NUMBER, BOOLEAN, LIST, OPERATION, INVALID, EMPTY
	}

	public ItemType getType() {
		return ItemType.INVALID;
	}

	public String asString() {
		return "BaseType()";
	}

	public Number toNumber() {
		return Float.NaN;
	}

	public boolean toBoolean() {
		return Boolean.FALSE;
	}

	public List<ParserInternalTypeBase> toList() {
		return Collections.emptyList();
	}

	public void setName(final String nameIn) {
		this.name = nameIn;
	}

	public String getName() {
		return this.name;
	}

	public void addItem(final ParserInternalTypeBase item) {
		this.items.put(item.getName(), item);
	}

	public Map<String, ParserInternalTypeBase> getChildren() {
		return this.items.isEmpty() ? Collections.emptyMap()
				: Collections.unmodifiableMap(this.items);
	}

	public ParserInternalTypeBase getParent() {
		return this.parent != null ? this.parent : EmptyType;
	}

	public String getValue() {
		return "";
	}
}
