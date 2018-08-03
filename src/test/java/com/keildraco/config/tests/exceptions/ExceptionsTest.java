package com.keildraco.config.tests.exceptions;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;

class ExceptionsTest {

	@Test
	final void testGenericParseException() {
		assertThrows(GenericParseException.class, () -> {
			throw new GenericParseException("Blargh!");
		});
	}

	@Test
	final void testIllegalParserStateException() {
		assertThrows(IllegalParserStateException.class, () -> {
			throw new IllegalParserStateException("Blargh!");
		});
	}

	@Test
	final void testUnknownStateException() {
		assertThrows(UnknownStateException.class, () -> {
			throw new UnknownStateException("Blargh!");
		});
	}

}
