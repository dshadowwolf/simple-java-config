package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class AbstractParserBaseTest {

	private static final String BLARGH = "BLARGH";

	/**
	 *
	 */
	@Test
	void testAbstractParserBase() {
		try {
			final TypeFactory f = new TypeFactory();
			final AbstractParserBase apb = new AbstractParserBaseTester(f, null, BLARGH);
			assertNotNull(apb);
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testSetFactory() {
		try {
			final TypeFactory f = new TypeFactory();
			final AbstractParserBase apb = new AbstractParserBaseTester(null, null, BLARGH);
			apb.setFactory(f);
			assertTrue(true, "AbstractParserBase.setFactory() works");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testGetFactory() {
		try {
			final TypeFactory f = new TypeFactory();
			final AbstractParserBase apb = new AbstractParserBaseTester(f, null, BLARGH);
			assertEquals(f, apb.getFactory());
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testSetParent() {
		try {
			final TypeFactory f = new TypeFactory();
			final AbstractParserBase apb = new AbstractParserBaseTester(f, null, BLARGH);
			apb.setParent(ParserInternalTypeBase.EMPTY_TYPE);
			assertTrue(true, "setParent() works");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testGetParent() {
		try {
			final TypeFactory f = new TypeFactory();
			final AbstractParserBase apb = new AbstractParserBaseTester(f, null, BLARGH);
			final IdentifierType it = new IdentifierType("test");
			apb.setParent(it);
			assertEquals(it, apb.getParent());
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testGetName() {
		try {
			final TypeFactory f = new TypeFactory();
			final AbstractParserBase apb = new AbstractParserBaseTester(f, null, BLARGH);
			assertEquals(BLARGH, apb.getName());
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testSetName() {
		try {
			final TypeFactory f = new TypeFactory();
			final AbstractParserBase apb = new AbstractParserBaseTester(f, null, BLARGH);
			apb.setName("BLECH");
			assertEquals("BLECH", apb.getName());
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 * @param parser
	 * @param data
	 * @return
	 * @throws IOException
	 * @throws IllegalParserStateException
	 * @throws UnknownStateException
	 * @throws GenericParseException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private ParserInternalTypeBase doParse(final AbstractParserBase parser, final String data)
			throws IOException, IllegalParserStateException, UnknownStateException,
			GenericParseException, NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		final InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
		final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
		final StreamTokenizer tok = new StreamTokenizer(br);
		final Tokenizer t = new Tokenizer(tok);
		Config.LOGGER.fatal("parser: %s%nis: %s%nbr: %s%ntok: %s%nt: %s%n", parser, is, br, tok, t);
		return parser.getState(t);
	}

	/**
	 *
	 */
	@Test
	void testGetState() {
		try {
			Config.registerKnownParts();
			final Path p = Paths.get("assets", "base-config-test.cfg");
			final String ts = String.join("/", p.toString().split("\\\\"));
			final URL tu = Config.class.getClassLoader().getResource(ts);
			assertNotNull(tu, "Resource could not be found!");
			final URI temp = tu.toURI();
			final InputStream is = temp.toURL().openStream();
			final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
			final StreamTokenizer tok = new StreamTokenizer(br);
			final Tokenizer t = new Tokenizer(tok);
			final AbstractParserBase apb = new AbstractParserBaseGetStateTester(Config.getFactory(),
					null, "BLARGH");
			Config.getFactory().registerParser(() -> apb, "BLARGH");
			apb.registerTransitions(Config.getFactory());
			final ParserInternalTypeBase res = apb.getState(t);
			final String matchVal = String.format("section {%n" + " magic = xyzzy%n"
					+ " all = ident3%n" + " blech {%n" + " magic = abcd%n" + "}%n%n"
					+ " key = [ list, op(! ident), ident2 ]%n" + "}%n");
			assertAll(
					() -> assertEquals(matchVal, res.getValue(),
							"result should be a specific value as a string"),
					() -> assertEquals(matchVal, res.getValueRaw()),
					() -> assertNotSame(res, ParserInternalTypeBase.EMPTY_TYPE,
							"AbstractParserBase.getState() works"),
					() -> assertThrows(IllegalParserStateException.class, () -> doParse(apb, ""),
							"throws on null input"),
					() -> assertEquals(ParserInternalTypeBase.EMPTY_TYPE,
							doParse(apb, "alpha(!bravo)"),
							"returns ParserInternalTypeBase.EmptyType on an internally caught exception"));
		} catch (final IOException | IllegalArgumentException | URISyntaxException
				| IllegalParserStateException | UnknownStateException | GenericParseException
				| NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	private static final class AbstractParserBaseTester extends AbstractParserBase {

		/**
		*
		* @param factoryIn
		* @param parentIn
		* @param nameIn
		*/
		AbstractParserBaseTester(@Nullable final TypeFactory factoryIn,
				@Nullable final ParserInternalTypeBase parentIn, final String nameIn) {
			super(factoryIn, parentIn, nameIn);
		}

		@Override
		public void registerTransitions(@Nullable final TypeFactory factory) {
			// intentionally blank
		}
	}

	/**
	 *
	 */
	private static final class AbstractParserBaseGetStateTester extends AbstractParserBase {

		/**
		*
		* @param factoryIn
		* @param parentIn
		* @param nameIn
		*/
		AbstractParserBaseGetStateTester(@Nullable final TypeFactory factoryIn,
				@Nullable final ParserInternalTypeBase parentIn, final String nameIn) {
			super(factoryIn, parentIn, nameIn);
		}

		@Override
		public void registerTransitions(final TypeFactory factory) {
			factory.registerStateTransition(this.getName().toUpperCase(Locale.ENGLISH),
					TokenType.IDENTIFIER, TokenType.OPEN_BRACE, "SECTION");
			factory.registerStateTransition(this.getName().toUpperCase(Locale.ENGLISH),
					TokenType.IDENTIFIER, TokenType.STORE, "KEYVALUE");
		}
	}
}
