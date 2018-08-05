package com.keildraco.config.interfaces;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

/**
 *
 * @author Daniel Hazelton
 *
 */
public abstract class ParserInternalTypeBase {

	/**
	 *
	 */
	private final ParserInternalTypeBase parent;

	/**
	 *
	 */
	private String name;

	/**
	 *
	 */
	private final Map<String, ParserInternalTypeBase> items;

	/**
	 *
	 */
	public static final ParserInternalTypeBase EMPTY_TYPE = new ParserInternalTypeBase("EMPTY") {

		@Override
		public boolean has(final String itemName) {
			return false;
		}

		@Nullable
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

		@Override
		public String getValue() {
			return "EMPTY";
		}

		@Override
		public String getValueRaw() {
			return this.getValue();
		}
	};

	/**
	 *
	 * @param nameIn
	 */
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

		if (parentIn == null) {
			this.parent = EMPTY_TYPE;
		} else {
			this.parent = parentIn;
		}

		this.items = new ConcurrentHashMap<>();
	}

	/**
	 *
	 * @param parentIn
	 * @param nameIn
	 * @param valueIn
	 */
	public ParserInternalTypeBase(@Nullable final ParserInternalTypeBase parentIn,
			final String nameIn, final String valueIn) {
		this(parentIn, valueIn);
		this.setName(nameIn);
	}

	/**
	 *
	 * @param itemName
	 * @return
	 */
	public ParserInternalTypeBase get(final String itemName) {
		final int index = itemName.indexOf('.');
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
				return this.getItems().get(nameRest);
			}
		} else if (index == 0) {
			throw new IllegalArgumentException("search keys cannot start with a '.'");
		} else if (this.has(itemName)) {
			return this.getItems().get(itemName);
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
			final boolean a = this.getItems().containsKey(nn);
			final boolean b = this.getItems().getOrDefault(nn, EMPTY_TYPE).has(rest);
			return a && b;
		}

		return this.getItems().containsKey(itemName);
	}

	/**
	 *
	 * @author Daniel Hazelton
	 *
	 */
	public enum ItemType {

		/**
		 *
		 */
		SECTION,

		/**
		 *
		 */
		IDENTIFIER,

		/**
		 *
		 */
		LIST,

		/**
		 *
		 */
		OPERATION,

		/**
		 *
		 */
		INVALID,

		/**
		 *
		 */
		EMPTY
	}

	/**
	 *
	 */
	public ItemType getType() {
		return ItemType.INVALID;
	}

	/**
	 *
	 * @param nameIn
	 */
	public void setName(final String nameIn) {
		this.name = nameIn;
	}

	/**
	 *
	 * @return
	 */
	public String getName() {
		return this.name;
	}

	/**
	 *
	 * @param item
	 */
	public void addItem(final ParserInternalTypeBase item) {
		this.getItems().put(item.getName(), item);
	}

	/**
	 *
	 * @return
	 */
	public Map<String, ParserInternalTypeBase> getChildren() {
		if (this.getItems().isEmpty()) {
			return Collections.emptyMap();
		}

		return Collections.unmodifiableMap(this.getItems());
	}

	/**
	 *
	 * @return
	 */
	public ParserInternalTypeBase getParent() {
		return this.parent;
	}

	/**
	 *
	 * @return
	 */
	public abstract String getValue();

	/**
	 *
	 * @return
	 */
	public abstract String getValueRaw();

	/**
	 *
	 * @return
	 */
	protected final Map<String, ParserInternalTypeBase> getItems() {
		return Collections.unmodifiableMap(this.items);
	}
}
