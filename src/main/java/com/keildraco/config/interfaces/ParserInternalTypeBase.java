package com.keildraco.config.interfaces;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

public class ParserInternalTypeBase {

	private final ParserInternalTypeBase parent;

	private String name;

	protected final Map<String, ParserInternalTypeBase> items;

	public static final ParserInternalTypeBase EMPTY_TYPE = new ParserInternalTypeBase("EMPTY") {

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

	public ParserInternalTypeBase(@Nullable final ParserInternalTypeBase parentIn,
			final String nameIn, @SuppressWarnings("unused") final String valueIn) {
		this(parentIn, nameIn);
	}

	/**
	 *
	 * @param itemName
	 * @return
	 */
	public ParserInternalTypeBase get(final String itemName) {
		int index = itemName.indexOf('.');
		if (index > 0) {
			final String nameBits = itemName.substring(0, itemName.indexOf('.'));
			final String nameRest = itemName.substring(itemName.indexOf('.') + 1);
			if (this.has(nameBits)) {
				/*
				 * this had an extraneous null check originally... if 'this.has()' returns true,
				 * then this.get() should not be null
				 */
				return this.get(nameBits).get(nameRest);
			} else if (this.getName().equalsIgnoreCase(nameBits) && this.has(nameRest)) {
				return this.items.get(nameRest);
			}
		} else if (index == 0) {
			throw new IllegalArgumentException("search keys cannot start with a '.'");
		} else if (this.has(itemName)) {
			return this.items.get(itemName);
		}
		return ParserInternalTypeBase.EMPTY_TYPE;
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
			final boolean b = this.items.getOrDefault(nn, EMPTY_TYPE).has(rest);
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
		if (this.items.isEmpty()) {
			return Collections.emptyMap();
		}

		return Collections.unmodifiableMap(this.items);
	}

	public ParserInternalTypeBase getParent() {
		if (this.parent != null) {
			return this.parent;
		}

		return EMPTY_TYPE;
	}

	public String getValue() {
		return "";
	}
}
