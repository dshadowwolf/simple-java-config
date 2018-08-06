package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
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
final class SectionParserTest {

	private static final String CAUGHT_EXCEPTION = "Caught exception running loadFile: ";
	private static final String EXCEPTION_GETTING = "Exception getting type instance for %s: %s";
	private static final String SECTION = "SECTION";

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
		final IStateParser parser = Config.getFactory().getParser(SECTION, null);
		final InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
		final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
		final StreamTokenizer tok = new StreamTokenizer(br);
		final Tokenizer t = new Tokenizer(tok);
		return parser.getState(t);
	}

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
				() -> assertNotSame(ParserInternalTypeBase.EMPTY_TYPE, this.doParse(validData),
						"standard parse works"),
				() -> assertThrows(GenericParseException.class, () -> this.doParse(earlyExit)),
				() -> assertThrows(IllegalParserStateException.class, () -> this.doParse(noData)),
				() -> assertThrows(UnknownStateException.class, () -> this.doParse(badData)));
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
			final SectionParser sp = new SectionParser(tf, null);
			sp.registerTransitions(tf);
			assertTrue(true, "was able to register transitions");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}
}
