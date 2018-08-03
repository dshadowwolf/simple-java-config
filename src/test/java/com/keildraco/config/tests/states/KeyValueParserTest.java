package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.*;

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
import com.keildraco.config.states.KeyValueParser;
import com.keildraco.config.tokenizer.Tokenizer;

class KeyValueParserTest {

	@Test
	final void testGetState() {
		try {
			Config.reset();
			Config.registerKnownParts();
			IStateParser p = Config.getFactory().getParser("KEYVALUE", null);
			String data = "item = value";
			InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
			InputStreamReader br = new InputStreamReader(is);
			StreamTokenizer tok = new StreamTokenizer(br);
			Tokenizer t = new Tokenizer(tok);
			ParserInternalTypeBase pb = p.getState(t);
			assertAll("result is correct", () -> assertTrue(pb != null, "result not null"),
					() -> assertEquals("item", pb.getName(), "name is correct"),
					() -> assertEquals("value", pb.getValueRaw(), "value is correct"));
		} catch (final IOException | IllegalArgumentException | NoSuchMethodException
				| InstantiationException | IllegalAccessException | InvocationTargetException
				| IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	@Test
	final void testKeyValueParser() {
		try {
			TypeFactory f = new TypeFactory();
			KeyValueParser kvp = new KeyValueParser(f, null);
			assertTrue(kvp != null, "Able to instantiate a KeyValueParser");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	@Test
	final void testRegisterTransitions() {
		try {
			TypeFactory f = new TypeFactory();
			KeyValueParser kvp = new KeyValueParser(f, null);
			kvp.registerTransitions(f);
			assertTrue(true, "was able to register transitions");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	private ParserInternalTypeBase doParse(String data)
			throws IOException, IllegalParserStateException, UnknownStateException,
			GenericParseException, NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		Config.reset();
		Config.registerKnownParts();
		IStateParser parser = Config.getFactory().getParser("KEYVALUE", null);
		InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
		InputStreamReader br = new InputStreamReader(is);
		StreamTokenizer tok = new StreamTokenizer(br);
		Tokenizer t = new Tokenizer(tok);
		Config.LOGGER.fatal("parser: %s%nis: %s%nbr: %s%ntok: %s%nt: %s%n", parser, is, br, tok, t);
		return parser.getState(t);
	}

	@Test
	final void testGetStateErrorRoutes() {
		String goodData = "item = value(! value)";
		String noData = "";
		assertAll(
				() -> assertThrows(IllegalParserStateException.class, () -> doParse(noData),
						"Illegal State, no data to parse"),
				() -> assertThrows(UnknownStateException.class, () -> doParse(goodData),
						"KEYVALUE cannot store OPERATION"));
	}

}
