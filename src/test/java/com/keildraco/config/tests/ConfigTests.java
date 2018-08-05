package com.keildraco.config.tests;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
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
import com.keildraco.config.exceptions.ParserRegistrationException;
import com.keildraco.config.exceptions.TypeRegistrationException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;

import javax.annotation.Nullable;

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
		Config.reset();
		assertThrows(UnknownStateException.class,
				() -> Config.getFactory().getParser("SECTION", null));
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
		Config.registerType(ItemType.INVALID, TypeThatThrows.class);
		Config.getFactory().registerParser(() -> { 
			if(!NullParser.flag) {
				NullParser.flag = true;
				return new NullParser(Config.getFactory(), null); 
			} else {
				return null; 
			}
		}, "NULLPARSER");
		assertAll(() -> assertThrows(TypeRegistrationException.class, () -> Config.getFactory().getType(null, "", "",
				ItemType.INVALID)),
				() -> assertThrows(ParserRegistrationException.class, () -> Config.registerParser("WILLTHROW", ParserThatThrows.class)),
				() -> assertThrows(UnknownStateException.class, () -> Config.getFactory().getParser("NULLPARSER", null)));
	}
	
	/**
	 *
	 * @author Daniel Hazelton
	 *
	 */
	private static final class NullParser extends AbstractParserBase {
		public static boolean flag = false;
		/**
		 *
		 * @param factory
		 * @param parent
		 */
		public NullParser(final TypeFactory factory, final ParserInternalTypeBase parent) {
			super(factory, parent, "NULLPARSER");
		}

		@Override
		public void registerTransitions(@Nullable final TypeFactory factory) {
			// blank
		}
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
		ParserThatThrows(final TypeFactory factory, final ParserInternalTypeBase parent)
				throws IllegalArgumentException {
			super(factory, parent, "TEST");
			throw new IllegalArgumentException("testing purposes only");
		}

		@Override
		public void registerTransitions(@Nullable final TypeFactory factory) {
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
				final String valueIn) throws GenericParseException {
			super(parentIn, nameIn, valueIn);
			throw new GenericParseException("testing purposes only");
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
