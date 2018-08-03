package com.keildraco.config.tests.factory;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.factory.Tokenizer;
import com.keildraco.config.factory.Tokenizer.Token;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;
import com.keildraco.config.states.SectionParser;
import com.keildraco.config.types.IdentifierType;

class TypeFactoryTest {

	@Test
	final void testTypeFactory() {
		try {
			TypeFactory f = new TypeFactory();
			assertTrue(f != null);
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	@Test
	final void testRegisterType() {
		try {
			TypeFactory f = new TypeFactory();
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
					ItemType.IDENTIFIER);
			assertTrue(true, "Able to register a type without an exception");
		} catch (Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	@Test
	final void testGetType() {
		try {
			TypeFactory f = new TypeFactory();
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
					ItemType.IDENTIFIER);
			assertTrue(f.getType(null, "", "", ItemType.IDENTIFIER) != null,
					"TypeFactory.getType() works");
		} catch (Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	@Test
	final void testRegisterParser() {
		try {
			TypeFactory f = new TypeFactory();
			f.registerParser(() -> {
				SectionParser sp = new SectionParser(f, null);
				sp.registerTransitions(f);
				return sp;
			}, "SECTION");
			assertTrue(true, "Able to register a parser without exceptions");
		} catch (Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/*
	 * registerStateTransition is actually tested in quite a few places, so we won't be testing it
	 * here
	 */

	@Test
	final void testGetParser() {
		try {
			TypeFactory f = new TypeFactory();
			f.registerParser(() -> {
				SectionParser sp = new SectionParser(f, null);
				sp.registerTransitions(f);
				return sp;
			}, "SECTION");
			assertTrue(f.getParser("SECTION", null) != null, "TypeFactory.getParser() works");
		} catch (Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	@Test
	final void testNextState() {
		try {
			Config.registerKnownParts();
			String data = "key = value";
			InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
			InputStreamReader br = new InputStreamReader(is);
			StreamTokenizer tok = new StreamTokenizer(br);
			Tokenizer t = new Tokenizer(tok);
			Token cur = t.peek();
			Token next = t.peekToken();
			IStateParser nextState = Config.getFactory().nextState("SECTION", cur, next);
			assertAll("TypeFactory.nextState() works", () -> assertTrue(nextState != null),
					() -> assertEquals("KEYVALUE", nextState.getName().toUpperCase()));
		} catch (Exception e) {
			Config.LOGGER.error("Exception registering type %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

}
