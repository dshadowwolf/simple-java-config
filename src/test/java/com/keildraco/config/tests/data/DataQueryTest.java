package com.keildraco.config.tests.data;

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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.DataQuery;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
class DataQueryTest {

	/**
	 *
	 * @throws Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		Config.reset();
		Config.registerKnownParts();
	}

	/**
	 *
	 */
	@Test
	final void testOf() {
		try {
			final Path p = Paths.get("assets", "base-config-test.cfg");
			final String ts = String.join("/", p.toString().split("\\\\"));
			final URL tu = Config.class.getClassLoader().getResource(ts);
			final URI temp = tu.toURI();
			final InputStream is = temp.toURL().openStream();
			final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
			final StreamTokenizer tok = new StreamTokenizer(br);
			final Tokenizer t = new Tokenizer(tok);
			final ParserInternalTypeBase pb = Config.getFactory().getParser("ROOT", null)
					.getState(t);
			final DataQuery c = DataQuery.of(pb);
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | URISyntaxException
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
	final void testGet() {
		final Path p = Paths.get("assets", "base-config-test.cfg");
		DataQuery c;
		try {
			c = com.keildraco.config.Config.loadFile(p);
			assertAll(() -> assertTrue(c.get("section.magic"), "basic test"),
					() -> assertFalse(c.get("section.dead"), "incorrect key"),
					() -> assertTrue(c.get("section"), "variant"),
					() -> assertTrue(c.get("section.magic.xyzzy"), "long test"),
					() -> assertFalse(c.get("nope"), "nonexistent bit, short"),
					() -> assertFalse(c.get("section.blech.dead"), "buried dead key"),
					() -> assertThrows(IllegalArgumentException.class, () -> c.get(".section")));
		} catch (final IOException | IllegalArgumentException | URISyntaxException
				| IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

}
