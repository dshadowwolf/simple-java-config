package com.keildraco.config.tests.states;

import static com.keildraco.config.testsupport.SupportClass.runParser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.SectionParser;
import static com.keildraco.config.Config.EMPTY_TYPE;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class SectionParserTest {

	private static final String CAUGHT_EXCEPTION = "Caught exception running loadFile: ";
	private static final String EXCEPTION_GETTING = "Exception getting type instance for %s: %s";
	private static final String SECTION = "SECTION";

	/**
	 *
	 */
	@Test
	void testGetState() {
		final String validData = "section { item = value }";
		final String earlyExit = "section { item = value";
		final String noData = "";
		final String badData = "section { [ item ] }";

		assertAll("",
				() -> assertNotSame(EMPTY_TYPE,
						runParser(validData, SECTION), "standard parse works"),
				() -> assertThrows(GenericParseException.class,
						() -> runParser(earlyExit, SECTION)),
				() -> assertThrows(IllegalParserStateException.class,
						() -> runParser(noData, SECTION)),
				() -> assertThrows(UnknownStateException.class, () -> runParser(badData, SECTION)));
	}

	/**
	 *
	 */
	@Test
	void testSectionParser() {
		try {
			final TypeFactory tf = new TypeFactory();
			final SectionParser sp = new SectionParser(tf, null);
			assertNotNull(sp, "Able to instantiate a SectionParser");
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
			final SectionParser sp = new SectionParser(tf, null);
			sp.registerTransitions(tf);
			assertTrue(true, "was able to register transitions");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}
}
