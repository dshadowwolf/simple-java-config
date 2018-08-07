package com.keildraco.config.types;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.keildraco.config.interfaces.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class SectionType extends ParserInternalTypeBase {

	/**
	 *
	 * @param name
	 */
	public SectionType(final String name) {
		this(null, name);
	}

	/**
	 *
	 * @param parent
	 * @param name
	 */
	public SectionType(@Nullable final ParserInternalTypeBase parent, final String name) {
		super(parent, name);
		if ((this.getName().isEmpty()) || (parent == null)) {
			this.setName("ROOT");
		}
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param value
	 */
	public SectionType(@Nullable final ParserInternalTypeBase parent, final String name,
			final String value) {
		this(parent, name);
	}

	@Nonnull
	@Override
	public ItemType getType() {
		return ItemType.SECTION;
	}

	@Override
	public String getValue() {
		final StringBuilder k = new StringBuilder();
		List<ParserInternalTypeBase> work = new ArrayList<>(this.getItems().values());
		Collections.reverse(work);

		if (!this.getName().equals("ROOT")) {
			k.append(String.format("%s {%n", this.getName()));
		}

		work.forEach(v -> k.append(String.format(" %s%n", v.getValue())));

		if (!this.getName().equals("ROOT")) {
			k.append(String.format("}%n"));
		}

		return k.toString();
	}

	@Override
	public String getValueRaw() {
		return this.getValue();
	}
}
