package com.keildraco.config.tests.data;

import static com.keildraco.config.testsupport.SupportClass.getTokenizerFromPath;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.net.URISyntaxException;
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
final class DataQueryTest {

	private static final String ASSETS = "assets";
	private static final String BASE_CONFIG_TEST_CFG = "base-config-test.cfg";
	private static final String CAUGHT_EXCEPTION = "Caught exception running loadFile: ";
	private static final String EXCEPTION_GETTING = "Exception getting type instance for %s: %s";
	private static final String LOAD_WORKED = "Load Worked? ";
	private static final String ROOT = "ROOT";

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
	void testOf() {
		try {
			final Tokenizer t = getTokenizerFromPath(Paths.get(ASSETS, BASE_CONFIG_TEST_CFG));
			final ParserInternalTypeBase pb = Config.getFactory().getParser(ROOT, null).getState(t);
			final DataQuery dq = DataQuery.of(pb);
			assertNotNull(dq, LOAD_WORKED);
		} catch (final IOException | IllegalArgumentException | URISyntaxException
				| IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testGet() {
		try {
			final Path p = Paths.get(ASSETS, BASE_CONFIG_TEST_CFG);
			final DataQuery dq = Config.loadFile(p);
			assertAll("", () -> assertTrue(dq.get("section.magic"), "basic test"),
					() -> assertFalse(dq.get("section.dead"), "incorrect key"),
					() -> assertTrue(dq.get("section"), "variant"),
					() -> assertTrue(dq.get("section.magic.xyzzy"), "long test"),
					() -> assertFalse(dq.get("nope"), "nonexistent bit, short"),
					() -> assertFalse(dq.get("section.blech.dead"), "buried dead key"),
					() -> assertThrows(IllegalArgumentException.class, () -> dq.get(".section")));
		} catch (final IOException | IllegalArgumentException | URISyntaxException
				| IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}
}
