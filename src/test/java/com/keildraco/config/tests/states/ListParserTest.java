package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

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
class ListParserTest {

	/**
	 *
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@BeforeAll
	final void setup() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Config.reset();
		Config.registerKnownParts();
	}

	/**
	 *
	 */
	@Test
	final void testGetState() {
		try {
			IStateParser p = Config.getFactory().getParser("LIST", null);
			String data = "[ alpha, beta, charlie(! delta) ]";
			InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
			InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
			StreamTokenizer tok = new StreamTokenizer(br);
			Tokenizer t = new Tokenizer(tok);
			ParserInternalTypeBase pb = p.getState(t);
			assertAll("result is correct", () -> assertTrue(pb != null, "result not null"),
					() -> assertTrue(pb.has("alpha"), "has member named alpha"),
					() -> assertFalse(pb.has("bravo"), "has no member named bravo"));
		} catch (final IOException | IllegalArgumentException | IllegalParserStateException
				| UnknownStateException | GenericParseException e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	final void testListParser() {
		try {
			TypeFactory f = new TypeFactory();
			ListParser op = new ListParser(f, null);
			assertTrue(op != null, "Able to instantiate a ListParser");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	final void testRegisterTransitions() {
		try {
			TypeFactory f = new TypeFactory();
			ListParser op = new ListParser(f, null);
			op.registerTransitions(f);
			assertTrue(true, "was able to register transitions");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
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
		IStateParser parser = Config.getFactory().getParser("LIST", null);
		InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
		InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
		StreamTokenizer tok = new StreamTokenizer(br);
		Tokenizer t = new Tokenizer(tok);
		Config.LOGGER.fatal("parser: %s%nis: %s%nbr: %s%ntok: %s%nt: %s%n", parser, is, br, tok, t);
		@SuppressWarnings("unused")
		ParserInternalTypeBase pb = parser.getState(t);
	}

	/**
	 *
	 */
	@Test
	final void testErrorStates() {
		String earlyEOF = "[ a, b, c";
		String noData = "";
		String badData = "[ a, ( ]";

		assertAll(() -> assertThrows(IllegalParserStateException.class, () -> doParse(noData)),
				() -> assertThrows(GenericParseException.class, () -> doParse(badData)),
				() -> assertThrows(GenericParseException.class, () -> doParse(earlyEOF)));
	}
}
