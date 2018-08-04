package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.OperationParser;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.OperationType;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class OperationParserTest {

	/**
	 *
	 */
	@Test
	void testGetState() {
		try {
			Config.reset();
			Config.registerKnownParts();
			final IStateParser p = Config.getFactory().getParser("OPERATION", null);
			final String data = "op(! ident)";
			final InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
			final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
			final StreamTokenizer tok = new StreamTokenizer(br);
			final Tokenizer t = new Tokenizer(tok);
			final OperationType opt = (OperationType) p.getState(t);
			assertAll("result is correct", () -> assertNotNull(opt, "result not null"),
					() -> assertEquals("op", opt.getName(), "name is correct"),
					() -> assertEquals("ident", opt.getValueRaw(), "value is correct"),
					() -> assertEquals('!', opt.getOperator()));
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
	void testOperationParser() {
		try {
			TypeFactory f = new TypeFactory();
			OperationParser op = new OperationParser(f, null);
			assertNotNull(op, "Able to instantiate a OperationParser");
		} catch (Exception e) {
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
	void testRegisterTransitions() {
		try {
			TypeFactory f = new TypeFactory();
			OperationParser op = new OperationParser(f, null);
			op.registerTransitions(f);
			assertTrue(true, "was able to register transitions");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 * @param data
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws IOException
	 * @throws IllegalParserStateException
	 * @throws UnknownStateException
	 * @throws GenericParseException
	 */
	private void doParse(final String data) throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, IOException,
			IllegalParserStateException, UnknownStateException, GenericParseException {
		Config.reset();
		Config.registerKnownParts();
		IStateParser parser = Config.getFactory().getParser("OPERATION", null);
		InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
		InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
		StreamTokenizer tok = new StreamTokenizer(br);
		Tokenizer t = new Tokenizer(tok);
		@SuppressWarnings("unused")
		ParserInternalTypeBase pb = parser.getState(t);
	}

	/**
	 *
	 */
	@Test
	void testErrorPaths() {
		String extraInParens = "op(! id ent)";
		String invalidOperator = "op(< ident)";
		String noOperator = "op(ident)";
		String notAnIdentifier = "op(~ id-ent)";
		String noWork = "";
		assertAll(() -> assertThrows(GenericParseException.class, () -> doParse(extraInParens)),
				() -> assertThrows(GenericParseException.class, () -> doParse(invalidOperator)),
				() -> assertThrows(GenericParseException.class, () -> doParse(noOperator)),
				() -> assertThrows(GenericParseException.class, () -> doParse(notAnIdentifier)),
				() -> assertThrows(IllegalParserStateException.class, () -> doParse(noWork)));
	}
}
