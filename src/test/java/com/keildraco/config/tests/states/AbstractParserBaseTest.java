package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.Config;
import com.keildraco.config.states.AbstractParserBase;
import com.keildraco.config.states.ListParser;
import com.keildraco.config.types.ParserInternalTypeBase;

@TestInstance(Lifecycle.PER_CLASS)
public final class AbstractParserBaseTest {

	@Test
	final void testSetFactory() {
		try {
			final AbstractParserBase p = new ListParser(null, "LIST");
			p.setFactory(Config.getFactory());
			assertTrue(true, "Expected setFactory() to not have an exception");
		} catch (final Exception e) {
			fail("Caught exception instantiating a new KeyValueParser: " + e.getMessage());
		}
	}

	@Test
	final void testGetFactory() {
		final AbstractParserBase p = new ListParser(Config.getFactory(), "LIST");
		assertEquals(Config.getFactory(), p.getFactory(),
				"p.getFactory() should equal the factory for the test suite");
	}

	@Test
	final void testSetParent() {
		try {
			final AbstractParserBase p = new ListParser(Config.getFactory(), "LIST");
			p.setParent(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected setParent() to not have an exception");
		} catch (final Exception e) {
			fail("Caught exception instantiating a new KeyValueParser: " + e.getMessage());
		}
	}
}
