package com.keildraco.config.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.DataQuery;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;

import javax.annotation.Nonnull;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class ConfigTests {

	/**
	 *
	 */
	@Test
	void testGetFactory() {
		assertNotNull(Config.getFactory());
	}

	/*
	 * There should be a "testRegisterType" and "testRegisterParser" here, but those are covered by
	 * "testRegisterKnownParts()"
	 */

	/**
	 * Test the automatic (using reflection) registration of known value types and parser states and
	 * state transitions.
	 *
	 * <p>
	 * Will fail if it catches any of a number of possible exceptions
	 */
	@Test
	void testRegisterKnownParts() {
		try {
			Config.registerKnownParts();
			assertTrue(true, "able to register known bits automatically and without exceptions");
		} catch (NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Config.LOGGER.fatal("Exception %s", e.toString());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::fatal);
			fail("Exception Caught");
		}
	}

	/**
	 *
	 */
	@Test
	void testReset() {
		try {
			Config.registerKnownParts();
			final IStateParser p = Config.getFactory().getParser("SECTION", null);
			Config.reset();
			final IStateParser q = Config.getFactory().getParser("SECTION", null);
			assertAll("parser prior to reset should not equal a parser post reset",
					() -> assertNull(q), () -> assertNotNull(p), () -> assertSame(p, q));
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			Config.LOGGER.fatal("Exception %s", e.toString());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::fatal);
			fail("Exception Caught");
		}
	}

	/*
	 * There would be a testParseStream() here, but it is actually called from all the various
	 * parse() functions. Same for testLoadFileURI(), actually, so neither gets a separate test.
	 * loadFileString() and loadFilePath() both call loadFileURI() - which calls parseStream().
	 */

	/**
	 *
	 */
	@Test
	void testLoadFilePath() {
		Path p = Paths.get("assets", "base-config-test.cfg");
		try {
			Config.reset();
			Config.registerKnownParts();
			final DataQuery c = com.keildraco.config.Config.loadFile(p);
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | URISyntaxException
				| NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException | IllegalParserStateException | UnknownStateException
				| GenericParseException e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testLoadFileString() {
		try {
			Config.reset();
			Config.registerKnownParts();
			final DataQuery c = com.keildraco.config.Config.loadFile("assets/base-config-test.cfg");
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | URISyntaxException
				| NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException | IllegalParserStateException | UnknownStateException
				| GenericParseException e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testParseString() {
		try {
			Config.reset();
			Config.registerKnownParts();
			final DataQuery c = com.keildraco.config.Config.parseString(
					"section { item = value\n item2 = [ list, op(! ident), op2(~ident2) ]\nsubsection { item3 = ident3 } }");
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | NoSuchMethodException
				| InstantiationException | IllegalAccessException | InvocationTargetException
				| IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testErrorStates() {
		Config.registerParser("WILLTHROW", ParserThatThrows.class);
		Config.registerType(ItemType.INVALID, TypeThatThrows.class);

		final IStateParser p = Config.getFactory().getParser("WILLTHROW", null);
		final ParserInternalTypeBase t = Config.getFactory().getType(null, "", "",
				ItemType.INVALID);
		assertAll(() -> assertNull(p), () -> assertNull(t));
	}

	/**
	 *
	 * @author Daniel Hazelton
	 *
	 */
	private static final class ParserThatThrows extends AbstractParserBase {

		/**
		 *
		 * @param factory
		 * @param b
		 * @throws IllegalAccessException
		 */
		ParserThatThrows(final TypeFactory factory, final ParserInternalTypeBase b)
				throws IllegalAccessException {
			super(factory, b, "TEST");
			throw new IllegalAccessException("testing purposes only");
		}

		@Override
		public void registerTransitions(@Nonnull final TypeFactory factory) {
			// not needed
		}
	}

	/**
	 *
	 * @author Daniel Hazelton
	 *
	 *
	 */
	private static final class TypeThatThrows extends ParserInternalTypeBase {

		/**
		 *
		 * @param parentIn
		 * @param nameIn
		 * @param valueIn
		 * @throws IllegalAccessException
		 */
		TypeThatThrows(final ParserInternalTypeBase parentIn, final String nameIn,
				final String valueIn) throws IllegalAccessException {
			super(parentIn, nameIn, valueIn);
			throw new IllegalAccessException("testing purposes only");
		}

		@Override
		public String getValue() {
			return "Abstract!";
		}

		@Override
		public String getValueRaw() {
			return this.getValue();
		}
	}
}
