package com.keildraco.config.tests.exceptions;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class ExceptionsTest {

	/**
	 *
	 */
	@Test
	void testGenericParseException() {
		assertThrows(GenericParseException.class, () -> {
			throw new GenericParseException("Blargh!");
		});
	}

	/**
	 *
	 */
	@Test
	void testIllegalParserStateException() {
		assertThrows(IllegalParserStateException.class, () -> {
			throw new IllegalParserStateException("Blargh!");
		});
	}

	/**
	 *
	 */
	@Test
	void testUnknownStateException() {
		assertThrows(UnknownStateException.class, () -> {
			throw new UnknownStateException("Blargh!");
		});
	}
}
