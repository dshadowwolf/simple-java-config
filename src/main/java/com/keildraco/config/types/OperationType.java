package com.keildraco.config.types;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.keildraco.config.interfaces.ParserInternalTypeBase;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class OperationType extends ParserInternalTypeBase {

	/**
	 *
	 */
	private String ident = "";

	/**
	 *
	 */
	private String operator;

	/**
	 *
	 * @param name
	 */
	public OperationType(final String name) {
		super(name);
		this.operator = "";
	}

	/**
	 *
	 * @param parent
	 * @param name
	 */
	public OperationType(@Nullable final ParserInternalTypeBase parent, final String name) {
		super(parent, name);
		this.operator = "";
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param value
	 */
	public OperationType(@Nullable final ParserInternalTypeBase parent, final String name,
			final String value) {
		this(parent, name);
		this.ident = value;
	}

	/**
	 *
	 * @param name
	 * @param value
	 */
	public OperationType(final String name, final String value) {
		this(null, name, value);
	}

	/**
	 *
	 * @param operatorIn
	 */
	public void setOperation(final String operatorIn) {
		this.operator = operatorIn.trim();
	}

	/**
	 *
	 * @return
	 */
	public int getOperator() {
		// lets see IDEA bitch about this :)
		return (int)this.operator.charAt(0);
	}

	@Nonnull
	@Override
	public String getValue() {
		return String.format("%s(%s %s)", this.getName(), this.operator, this.ident);
	}

	@Nonnull
	@Override
	public String getValueRaw() {
		return this.ident;
	}

	@Nonnull
	@Override
	public ItemType getType() {
		return ItemType.OPERATION;
	}
}
