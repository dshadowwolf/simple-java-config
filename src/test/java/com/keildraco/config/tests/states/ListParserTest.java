package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.ListParser;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
final class ListParserTest {

	private static final String CAUGHT_EXCEPTION = "Caught exception running loadFile: ";
	private static final String EXCEPTION_GETTING = "Exception getting type instance for %s: %s";
	private static final String LIST = "LIST";

	/**
	 *
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@BeforeAll
	void setUp() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Config.reset();
		Config.registerKnownParts();
	}

	/**
	 *
	 */
	@Test
	void testGetState() {
		try {
			final IStateParser p = Config.getFactory().getParser(LIST, null);
			final String data = "[ alpha, beta, charlie(! delta) ]";
			final InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
			final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
			final StreamTokenizer tok = new StreamTokenizer(br);
			final Tokenizer t = new Tokenizer(tok);
			final ParserInternalTypeBase pb = p.getState(t);
			assertAll("result is correct",
					() -> assertNotNull(pb, "result not null"),
					() -> assertTrue(pb.has("alpha"), "has member named alpha"),
					() -> assertFalse(pb.has("bravo"), "has no member named bravo"));
		} catch (final IOException | IllegalArgumentException | IllegalParserStateException
				| UnknownStateException | GenericParseException e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testListParser() {
		try {
			final TypeFactory tf = new TypeFactory();
			final ListParser op = new ListParser(tf, null);
			assertNotNull(op, "Able to instantiate a ListParser");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(),
					e.getMessage());
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
			final ListParser op = new ListParser(tf, null);
			op.registerTransitions(tf);
			assertTrue(true, "was able to register transitions");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 * @param data
	 * @throws IOException
	 * @throws IllegalParserStateException
	 * @throws UnknownStateException
	 * @throws GenericParseException
	 */
	private void doParse(final String data) throws IOException, IllegalParserStateException,
			UnknownStateException, GenericParseException {
		final IStateParser parser = Config.getFactory().getParser(LIST, null);
		final InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
		final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
		final StreamTokenizer tok = new StreamTokenizer(br);
		final Tokenizer t = new Tokenizer(tok);
		parser.getState(t);
	}

	/**
	 *
	 */
	@Test
	void testErrorNoData() {
		assertThrows(IllegalParserStateException.class, () -> this.doParse(""));
	}
	
	/**
	 *
	 */
	@Test
	void testErrorBadData() {
		assertThrows(GenericParseException.class, () -> this.doParse("[ a, ( ]"));
	}
	
	/**
	 *
	 */
	@Test
	void testErrorEarlyEOF() {
		assertThrows(GenericParseException.class, () -> this.doParse("[ ash, blood, choices"));
	}	
}
