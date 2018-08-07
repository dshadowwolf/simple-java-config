package com.keildraco.config.types;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.keildraco.config.data.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import static com.keildraco.config.Config.EMPTY_TYPE;

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
	 * @param name
	 */
	public ListType(final String name) {
		this(name, Collections.emptyList());
	}

	/**
	 *
	 * @param name
	 * @param values
	 */
	public ListType(final String name, final Collection<ParserInternalTypeBase> values) {
		this(null, name, values);
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param values
	 */
	public ListType(@Nullable final ParserInternalTypeBase parent, final String name,
			final Collection<ParserInternalTypeBase> values) {
		super(parent, name);
		this.value = new LinkedList<>(values);
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
	public boolean has(final String itemName) {
		final String caseMatched = itemName.toLowerCase(Locale.getDefault());
		return this.value.stream().map(ParserInternalTypeBase::getName)
				.anyMatch(caseMatched::equals);
	}

	@Override
	public ParserInternalTypeBase get(final String itemName) {
		final String caseMatched = itemName.toLowerCase(Locale.getDefault());

		if (!this.has(caseMatched)) {
			return EMPTY_TYPE;
		}

		return this.value.stream().filter(pitb -> caseMatched.equals(pitb.getName()))
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
	private static String getItemValue(final ParserInternalTypeBase item) {
		return item.getValue();
	}

	@Nonnull
	@Override
	public String getValue() {
		final String format = this.getValueRaw();
		if (this.getName().isEmpty()) {
			return format;
		}
		return String.format("%s = %s", this.getName(), format);
	}

	@Nonnull
	@Override
	public String getValueRaw() {
		return String.format("[ %s ]",
				this.value.stream().map(ListType::getItemValue).collect(Collectors.joining(", ")));
	}
}
