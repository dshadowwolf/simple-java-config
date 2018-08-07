package com.keildraco.config.tests.states;

import static com.keildraco.config.testsupport.SupportClass.getTokenizerFromString;
import static com.keildraco.config.testsupport.SupportClass.runParser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.KeyValueParser;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class KeyValueParserTest {

	private static final String CAUGHT_EXCEPTION = "Caught exception running loadFile: ";
	private static final String EXCEPTION_GETTING = "Exception getting type instance for {}: {}";
	private static final String KEYVALUE = "KEYVALUE";

	/**
	 *
	 */
	@Test
	void testGetState() {
		try {
			Config.reset();
			Config.registerKnownParts();
			final IStateParser p = Config.getFactory().getParser(KEYVALUE, null);
			final Tokenizer t = getTokenizerFromString("item = value");
			final ParserInternalTypeBase pb = p.getState(t);
			assertAll("result is correct", () -> assertNotNull(pb, "result not null"),
					() -> assertEquals("item", pb.getName(), "name is correct"),
					() -> assertEquals("value", pb.getValueRaw(), "value is correct"));
		} catch (final IOException | IllegalArgumentException | NoSuchMethodException
				| InstantiationException | IllegalAccessException | InvocationTargetException
				| IllegalParserStateException | UnknownStateException | GenericParseException
				| URISyntaxException e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testKeyValueParser() {
		try {
			final TypeFactory tf = new TypeFactory();
			final KeyValueParser kvp = new KeyValueParser(tf, null);
			assertNotNull(kvp, "Able to instantiate a KeyValueParser");
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
			final KeyValueParser kvp = new KeyValueParser(tf, null);
			kvp.registerTransitions(tf);
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
	void testGetStateErrorRoutes() {
		final String goodData = "item = value(! value)";
		final String noData = "";
		assertAll("",
				() -> assertThrows(IllegalParserStateException.class,
						() -> runParser(noData, KEYVALUE), "Illegal State, no data to parse"),
				() -> assertThrows(UnknownStateException.class, () -> runParser(goodData, KEYVALUE),
						"KEYVALUE cannot store OPERATION"));
	}
}
