package com.keildraco.config.testsupport;

import javax.annotation.Nonnull;

import com.keildraco.config.interfaces.ParserInternalTypeBase;

public final class TypeInstanceProvider {
	private static final String	ABSTRACT				= "Abstract!";

	/**
	 *
	 * @param name
	 * @return
	 */
	public static ParserInternalTypeBase getInstance(final String name) {
		return new ParserInternalTypeBase(name) {

			@Override
			@Nonnull
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return ABSTRACT;
			}
		};
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @return
	 */
	public static ParserInternalTypeBase getInstance(final ParserInternalTypeBase parent,
			final String name) {
		return new ParserInternalTypeBase(parent, name) {

			@Nonnull
			@Override
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return ABSTRACT;
			}
		};
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param value
	 * @return
	 */
	public static ParserInternalTypeBase getInstance(final ParserInternalTypeBase parent,
			final String name, final String value) {
		return new ParserInternalTypeBase(parent, name, value) {

			@Nonnull
			@Override
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return ABSTRACT;
			}
		};
	}
}
