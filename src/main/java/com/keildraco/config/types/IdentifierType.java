package com.keildraco.config.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.keildraco.config.data.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import static com.keildraco.config.Config.EMPTY_TYPE;

import java.util.Locale;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class IdentifierType extends ParserInternalTypeBase {

	/**
	 *
	 */
	private final String ident;

	/**
	 *
	 * @param name
	 */
	public IdentifierType(final String name) {
		this(name, name);
	}

	/**
	 *
	 * @param name
	 * @param value
	 */
	public IdentifierType(final String name, final String value) {
		this(null, name, value);
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param value
	 */
	public IdentifierType(@Nullable final ParserInternalTypeBase parent, final String name,
			final String value) {
		super(parent, name);
		this.ident = value.toLowerCase(Locale.getDefault());
	}

	@Override
	public void addItem(final ParserInternalTypeBase item) {
		// identifiers - that is, key-value pairs - can't store more than an identifier they equal,
		// so this method gets stubbed
	}

	@Override
	public boolean has(final String itemName) {
		return this.getName().equals(itemName) || this.ident.equals(itemName);
	}

	@Nonnull
	@Override
	public ParserInternalTypeBase get(final String itemName) {
		if (this.has(itemName)) {
			return this;
		}
		return EMPTY_TYPE;
	}

	@Nonnull
	@Override
	public String getValue() {
		if (this.getName().equals(this.ident)) {
			return this.ident;
		}
		return String.format("%s = %s", this.getName(), this.ident);
	}

	@Nonnull
	@Override
	public String getValueRaw() {
		return this.ident;
	}

	@Nonnull
	@Override
	public ItemType getType() {
		return ItemType.IDENTIFIER;
	}
}
