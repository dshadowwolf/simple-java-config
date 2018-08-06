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

	private static final String ABSTRACT = "Abstract!";
	private static final String ASSETS = "assets";
	private static final String BASE_CONFIG_TEST_CFG = "base-config-test.cfg";
	private static final String CAUGHT_EXCEPTION_RUNNING_LOADFILE = "Caught exception running loadFile: ";
	private static final String EXCEPTION = "Exception %s";
	private static final String EXCEPTION_CAUGHT = "Exception Caught";
	private static final String EXCEPTION_GETTING_TYPE_INSTANCE_FOR = "Exception getting type instance for %s: %s";
	private static final String LOAD_WORKED = "Load Worked? ";
	private static final String NULLPARSER = "NULLPARSER";
	private static final String SECTION = "SECTION";
	private static final String TEST = "TEST";
	private static final String TESTING_PURPOSES_ONLY = "Testing purposes only";
	private static final String WILLTHROW = "WILLTHROW";

	/**
	 *
	 */
	@Test
	void testGetFactory() {
		assertNotNull(Config.getFactory(), "");
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
		} catch (final NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			Config.LOGGER.fatal(EXCEPTION, e.toString());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::fatal);
			fail(EXCEPTION_CAUGHT);
		}
	}

	/**
	 *
	 */
	@Test
	void testReset() {
		Config.reset();
		assertThrows(UnknownStateException.class,
				() -> Config.getFactory().getParser(SECTION, null), "");
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
		final Path p = Paths.get(ASSETS, BASE_CONFIG_TEST_CFG);
		try {
			Config.reset();
			Config.registerKnownParts();
			final DataQuery dq = com.keildraco.config.Config.loadFile(p);
			assertNotNull(dq, LOAD_WORKED);
		} catch (final IOException | IllegalArgumentException | URISyntaxException
				| NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException | IllegalParserStateException | UnknownStateException
				| GenericParseException e) {
			Config.LOGGER.error(EXCEPTION_GETTING_TYPE_INSTANCE_FOR, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION_RUNNING_LOADFILE + e);
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
			final DataQuery dq = com.keildraco.config.Config.loadFile(ASSETS + '/' + BASE_CONFIG_TEST_CFG);
			assertNotNull(dq, LOAD_WORKED);
		} catch (final IOException | IllegalArgumentException | URISyntaxException
				| NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException | IllegalParserStateException | UnknownStateException
				| GenericParseException e) {
			Config.LOGGER.error(EXCEPTION_GETTING_TYPE_INSTANCE_FOR, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION_RUNNING_LOADFILE + e);
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
			final DataQuery dq = com.keildraco.config.Config.parseString(
					"section { item = value\n item2 = [ list, op(! ident), op2(~ident2) ]\nsubsection { item3 = ident3 } }");
			assertNotNull(dq, LOAD_WORKED);
		} catch (final IOException | IllegalArgumentException | NoSuchMethodException
				| InstantiationException | IllegalAccessException | InvocationTargetException
				| IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error(EXCEPTION_GETTING_TYPE_INSTANCE_FOR, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION_RUNNING_LOADFILE + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testErrorStates() {
		Config.registerType(ItemType.INVALID, TypeThatThrows.class);
		Config.getFactory().registerParser(() -> {
			if (NullParser.flag) {
				return null;
			} else {
				NullParser.flag = true;
				return new NullParser(Config.getFactory(), null);
			}
		}, NULLPARSER);
		assertAll("",
				() -> assertThrows(TypeRegistrationException.class, () -> Config.getFactory().getType(null, "", "",
				ItemType.INVALID), ""),
				() -> assertThrows(ParserRegistrationException.class, () -> Config.registerParser(WILLTHROW, ParserThatThrows.class), ""),
				() -> assertThrows(UnknownStateException.class, () -> Config.getFactory().getParser(NULLPARSER, null), ""),
				() -> assertThrows(IOException.class, () -> Config.loadFile("assets/this-doesnt-exist.cfg")));
	}

	/**
	 *
	 * @author Daniel Hazelton
	 *
	 */
	private static final class NullParser extends AbstractParserBase {

		static boolean flag = false;

		/**
		 *
		 * @param factory
		 * @param parent
		 */
		NullParser(final TypeFactory factory, final ParserInternalTypeBase parent) {
			super(factory, parent, NULLPARSER);
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
		 * @param parent
		 * @throws IllegalAccessException
		 */
		ParserThatThrows(final TypeFactory factory, final ParserInternalTypeBase parent)
				throws IllegalArgumentException {
			super(factory, parent, TEST);
			throw new IllegalArgumentException(TESTING_PURPOSES_ONLY);
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
			throw new GenericParseException(TESTING_PURPOSES_ONLY);
		}

		@Override
		public String getValue() {
			return ABSTRACT;
		}

		@Override
		public String getValueRaw() {
			return this.getValue();
		}
	}	
}
