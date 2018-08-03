package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
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
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.SectionParser;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
class SectionParserTest {

	/**
	 *
	 * @param data
	 * @return
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws IllegalParserStateException
	 * @throws UnknownStateException
	 * @throws GenericParseException
	 */
	private ParserInternalTypeBase doParse(final String data) throws NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException, IOException,
			IllegalParserStateException, UnknownStateException, GenericParseException {
		Config.reset();
		Config.registerKnownParts();
		IStateParser parser = Config.getFactory().getParser("SECTION", null);
		InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
		InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
		StreamTokenizer tok = new StreamTokenizer(br);
		Tokenizer t = new Tokenizer(tok);
		return parser.getState(t);
	}

	/**
	 *
	 */
	@Test
	final void testGetState() {
		String validData = "section { item = value }";
		String earlyExit = "section { item = value";
		String noData = "";
		String badData = "section { [ item ] }";

		assertAll(
				() -> assertTrue(doParse(validData) != ParserInternalTypeBase.EMPTY_TYPE,
						"standard parse works"),
				() -> assertThrows(GenericParseException.class, () -> doParse(earlyExit)),
				() -> assertThrows(IllegalParserStateException.class, () -> doParse(noData)),
				() -> assertThrows(UnknownStateException.class, () -> doParse(badData)));
	}

	/**
	 *
	 */
	@Test
	final void testSectionParser() {
		try {
			TypeFactory f = new TypeFactory();
			SectionParser sp = new SectionParser(f, null);
			assertTrue(sp != null, "Able to instantiate a SectionParser");
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
			SectionParser sp = new SectionParser(f, null);
			sp.registerTransitions(f);
			assertTrue(true, "was able to register transitions");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}
}
