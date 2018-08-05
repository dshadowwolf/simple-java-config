package com.keildraco.config.tests.factory;

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
import java.util.Locale;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.Token;
import com.keildraco.config.exceptions.UnknownParseTreeTypeException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;
import com.keildraco.config.states.SectionParser;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class TypeFactoryTest {

	/**
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws NoSuchMethodException 
	 *
	 */
	@BeforeEach
	void setup() throws NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		Config.reset();
		Config.registerKnownParts();
	}
	
	/**
	 *
	 */
	@Test
	void testTypeFactory() {
		try {
			final TypeFactory f = new TypeFactory();
			assertNotNull(f);
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
	void testRegisterType() {
		try {
			final TypeFactory f = new TypeFactory();
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
					ItemType.IDENTIFIER);
			assertTrue(
					f.getType(ParserInternalTypeBase.EMPTY_TYPE, "Blargh", "Blech",
							ItemType.IDENTIFIER) instanceof IdentifierType,
					"Able to register a type without an exception");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testGetType() {
		try {
			final TypeFactory f = new TypeFactory();
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
					ItemType.IDENTIFIER);
			assertNotNull(f.getType(null, "", "", ItemType.IDENTIFIER),
					"TypeFactory.getType() works");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testRegisterParser() {
		try {
			final TypeFactory f = new TypeFactory();
			f.registerParser(() -> {
				final SectionParser sp = new SectionParser(f, null);
				sp.registerTransitions(f);
				return sp;
			}, "SECTION");
			assertTrue(true, "Able to register a parser without exceptions");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/*
	 * registerStateTransition is actually tested in quite a few places, so we won't be testing it
	 * here
	 */

	/**
	 *
	 */
	@Test
	void testGetParser() {
		try {
			final TypeFactory f = new TypeFactory();
			f.registerParser(() -> {
				final SectionParser sp = new SectionParser(f, null);
				sp.registerTransitions(f);
				return sp;
			}, "SECTION");
			assertNotNull(f.getParser("SECTION", null), "TypeFactory.getParser() works");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testNextState() {
		try {
			final String data = "key = value";
			final InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
			final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
			final StreamTokenizer tok = new StreamTokenizer(br);
			final Tokenizer t = new Tokenizer(tok);
			final Token cur = t.peek();
			final Token next = t.peekToken();
			final IStateParser nextState = Config.getFactory().nextState("SECTION", cur, next);
			assertAll("TypeFactory.nextState() works", () -> assertNotNull(nextState),
					() -> assertEquals("KEYVALUE",
							nextState.getName().toUpperCase(Locale.ENGLISH)));
		} catch (final IOException e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}
	
	/**
	 *
	 */
	@Test
	void testGetNonexistentType() {
		assertThrows(UnknownParseTreeTypeException.class, () -> Config.getFactory().getType(null, "blargh", "blech", ItemType.INVALID));
	}
	
	/**
	 *
	 */
	@Test
	void testGetWithParent() {
		assertNotNull(Config.getFactory().getParser("SECTION", ParserInternalTypeBase.EMPTY_TYPE));
	}
}
