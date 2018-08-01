package com.keildraco.config.tests.factory;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.IStateParser;
import com.keildraco.config.states.KeyValueParser;
import com.keildraco.config.states.ListParser;
import com.keildraco.config.states.SectionParser;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;
import com.keildraco.config.types.SectionType;

public final class TypeFactoryTest {

	@Test
	public final void testTypeFactory() {
		try {
			@SuppressWarnings("unused")
			final TypeFactory f = new TypeFactory();
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception " + e.getMessage()
					+ " when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testRegisterType() {
		try {
			final TypeFactory f = new TypeFactory();
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
					ItemType.BOOLEAN);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception " + e.getMessage()
					+ " when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testGetType() {
		try {
			final TypeFactory f = new TypeFactory();
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
					ItemType.BOOLEAN);
			assertNotEquals(ParserInternalTypeBase.EmptyType,
					f.getType(null, "", "", ItemType.BOOLEAN));
		} catch (final Exception e) {
			fail("Caught exception " + e.getMessage()
					+ " when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testRegisterParser() {
		try {
			final TypeFactory f = new TypeFactory();
			f.registerParser(() -> new SectionParser(f, null, ""), "SECTION");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception " + e.getMessage()
					+ " when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testGetParser() {
		try {
			final TypeFactory f = new TypeFactory();
			f.registerParser(() -> new SectionParser(f, null, ""), "SECTION");
			final IStateParser g = f.getParser("SECTION", null);
			assertNotNull(g, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception " + e.getMessage()
					+ " when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testParseTokens() {
		try {
			final TypeFactory f = new TypeFactory();
			f.registerParser(() -> new ListParser(f, "LIST"), "LIST");
			f.registerParser(() -> new KeyValueParser(f, "KEYVALUE"), "KEYVALUE");
			f.registerParser(() -> new SectionParser(f, null, ""), "SECTION");
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
					ItemType.IDENTIFIER);
			f.registerType((parent, name, value) -> new ListType(parent, name, value),
					ItemType.LIST);
			f.registerType((parent, name, value) -> new OperationType(parent, name, value),
					ItemType.OPERATION);
			f.registerType((parent, name, value) -> new SectionType(parent, name, value),
					ItemType.SECTION);
			final String testString = "section1 {\nidentifier = false\nsection2 {\nident2 = true\n}\n}\n\n";
			final InputStreamReader isr = new InputStreamReader(
					IOUtils.toInputStream(testString, StandardCharsets.UTF_8),
					StandardCharsets.UTF_8);
			final StreamTokenizer t = new StreamTokenizer(isr);
			t.commentChar('#');
			t.wordChars('_', '_');
			t.wordChars('-', '-');
			t.slashSlashComments(true);
			t.slashStarComments(true);
			final ParserInternalTypeBase z = f.parseTokens("SECTION", null, t, "ROOT");
			assertAll(
					"Expect result to have a \"section1\" containing a \"section2\" and an \"identifier\" and for \"section2\" to have \"ident2\"",
					() -> z.has("section1"), () -> z.get("section1").has("section2"),
					() -> z.get("section1").has("identifier"),
					() -> z.get("section1").get("section2").has("ident2"));
		} catch (final Exception e) {
			fail("Caught exception " + e.getMessage()
					+ " when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testRequestMissingParser() {
		Config.reset();
		Config.registerKnownParts();
		final IStateParser p = Config.getFactory().getParser("NOSUCHPARSER", null);
		assertNull(p, "parser with name \"NOSUCHPARSER\" was never registered, should return null");
	}

	@Test
	public final void testMissingParserParseTokens() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "section1 {\nidentifier = false\nsection2 {\nident2 = true\n}\n}\n\n";
		final InputStreamReader isr = new InputStreamReader(
				IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase z = Config.getFactory().parseTokens("NOSUCHPARSER", null, t,
				"ROOT");
		assertEquals(ParserInternalTypeBase.EmptyType, z,
				"parseTokens with nonexistent parser should return EmptyType");
	}
}
