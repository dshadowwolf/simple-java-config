package com.keildraco.config.tests.states;

import static com.keildraco.config.testsupport.SupportClass.runParser;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.RootState;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class RootStateTest {

	private static final String CAUGHT_EXCEPTION = "Caught exception running loadFile: ";
	private static final String EXCEPTION_GETTING = "Exception getting type instance for %s: %s";

	@BeforeAll
	static void setup() {
		Config.reset();
		try {
			Config.registerKnownParts();
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testRootState() {
		try {
			final TypeFactory tf = new TypeFactory();
			final RootState rs = new RootState(tf, null);
			assertNotNull(rs, "Able to instantiate a RootState");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testRegisterTransitions() {
		try {
			final TypeFactory tf = new TypeFactory();
			final RootState rs = new RootState(tf, null);
			rs.registerTransitions(tf);
			assertTrue(true, "was able to register transitions");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testErrorStateInParse() {
		try {
			assertEquals(ParserInternalTypeBase.EMPTY_TYPE, runParser("error(! state)", "ROOT"));
		} catch (IllegalParserStateException | UnknownStateException | GenericParseException
				| NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException | IOException | URISyntaxException e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testErrorStateNoInput() {
		assertThrows(IllegalParserStateException.class, () -> runParser("", "ROOT"));
	}

}
