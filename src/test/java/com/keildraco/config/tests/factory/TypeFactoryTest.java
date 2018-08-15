package com.keildraco.config.tests.factory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Locale;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.UnknownParseTreeTypeException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.testsupport.MockSource;
import com.keildraco.config.types.IdentifierType;

import static com.keildraco.config.Config.EMPTY_TYPE;
import static com.keildraco.config.data.Constants.ParserNames.KEYVALUE;
import static com.keildraco.config.data.Constants.ParserNames.SECTION;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class TypeFactoryTest {

	private static final String	BLARGH					= "Blargh";
	private static final String	BLECH					= "Blech";
	private static final String	CAUGHT_EXCEPTION		= "Caught exception running loadFile: ";
	private static final String	EXCEPTION_GETTING		= "Exception getting instance for {}: {}";
	private static final String	EXCEPTION_REGISTERING	= "Exception registering type {}: {}";

	/**
	 * @throws InvocationTargetException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws NoSuchMethodException
	 *
	 */
	@BeforeEach
	void setUp() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Config.reset();
		Config.registerKnownParts();
	}

	/**
	 *
	 */
	@Test
	void testTypeFactory() {
		try {
			final TypeFactory tf = new TypeFactory();
			assertNotNull(tf, "TypeFactory can have an instance created");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testRegisterType() {
		try {
			final TypeFactory tf = new TypeFactory();
			tf.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
					ItemType.IDENTIFIER);
			assertTrue(
					tf.getType(EMPTY_TYPE, BLARGH, BLECH,
							ItemType.IDENTIFIER) instanceof IdentifierType,
					"Able to register a type without an exception");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testGetType() {
		try {
			final TypeFactory tf = new TypeFactory();
			tf.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
					ItemType.IDENTIFIER);
			assertNotNull(tf.getType(null, "", "", ItemType.IDENTIFIER),
					"TypeFactory.getType() works");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_REGISTERING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testRegisterParser() {
		try {
			final TypeFactory tf = new TypeFactory();
			tf.registerParser(() -> MockSource.mockSectionParser(), SECTION);
			assertTrue(true, "Able to register a parser without exceptions");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_REGISTERING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
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
			final TypeFactory tf = new TypeFactory();
			tf.registerParser(() -> MockSource.mockSectionParser(), SECTION);
			assertNotNull(tf.getParser(SECTION, null), "TypeFactory.getParser() works");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_REGISTERING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testNextState() {
			final TypeFactory tf = new TypeFactory();
			tf.registerStateTransition(SECTION, TokenType.IDENTIFIER, TokenType.STORE, KEYVALUE);
			tf.registerParser(() -> MockSource.mockKeyValueParser(), KEYVALUE);
			Token cur = new Token("key");
			Token next = new Token("=");
			final IStateParser nextState = Config.getFactory().nextState(SECTION, cur, next);
			assertAll("TypeFactory.nextState() works", () -> assertNotNull(nextState, ""),
					() -> assertEquals(KEYVALUE, nextState.getName().toUpperCase(Locale.ENGLISH),
							""));
	}

	/**
	 *
	 */
	@Test
	void testGetNonexistentType() {
		final TypeFactory tf = new TypeFactory();
		assertThrows(UnknownParseTreeTypeException.class,
				() -> tf.getType(null, BLARGH, BLECH, ItemType.INVALID));
	}

	/**
	 *
	 */
	@Test
	void testGetWithParent() {
		final TypeFactory tf = new TypeFactory();
		tf.registerParser(() -> MockSource.mockSectionParser(), SECTION);
		assertNotNull(tf.getParser(SECTION, EMPTY_TYPE), "");
	}
}
