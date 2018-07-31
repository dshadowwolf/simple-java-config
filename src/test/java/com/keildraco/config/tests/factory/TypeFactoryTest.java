package com.keildraco.config.tests.factory;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.*;
import com.keildraco.config.types.*;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

public class TypeFactoryTest {
	@Test
	public final void testTypeFactory() {
		try {
			@SuppressWarnings("unused")
			TypeFactory f = new TypeFactory();
			assertTrue(true, "Expected no exception");
		} catch(Exception e) {
			fail("Caught exception "+e.getMessage()+" when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testRegisterType() {
		try {
			TypeFactory f = new TypeFactory();
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value), ItemType.BOOLEAN);
			assertTrue(true, "Expected no exception");
		} catch(Exception e) {
			fail("Caught exception "+e.getMessage()+" when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testGetType() {
		try {
			TypeFactory f = new TypeFactory();
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value), ItemType.BOOLEAN);
			assertNotEquals(ParserInternalTypeBase.EmptyType, f.getType(null, "", "", ItemType.BOOLEAN));
		} catch(Exception e) {
			fail("Caught exception "+e.getMessage()+" when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testRegisterParser() {
		try {
			TypeFactory f = new TypeFactory();
			f.registerParser(() -> new SectionParser(f,null,""), "SECTION");
			assertTrue(true, "Expected no exception");
		} catch(Exception e) {
			fail("Caught exception "+e.getMessage()+" when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testGetParser() {
		try {
			TypeFactory f = new TypeFactory();
			f.registerParser(() -> new SectionParser(f,null,""), "SECTION");
			IStateParser g = f.getParser("SECTION", null);
			assertNotNull(g, "Expected no exception");
		} catch(Exception e) {
			fail("Caught exception "+e.getMessage()+" when trying to instantiate a TypeFactory");
		}
	}

	@Test
	public final void testParseTokens() {
		try {
			TypeFactory f = new TypeFactory();
			f.registerParser(() -> new ListParser(f, "LIST"), "LIST");
			f.registerParser(() -> new KeyValueParser(f, "KEYVALUE"), "KEYVALUE");
			f.registerParser(() -> new SectionParser(f, null, ""), "SECTION");
			f.registerType((parent, name, value) -> new IdentifierType(parent, name, value), ItemType.IDENTIFIER);
			f.registerType((parent, name, value) -> new ListType(parent, name, value), ItemType.LIST);
			f.registerType((parent, name, value) -> new OperationType(parent, name, value), ItemType.OPERATION);
			f.registerType((parent, name, value) -> new SectionType(parent, name, value), ItemType.SECTION);
			String testString = "section1 {\nidentifier = false\nsection2 {\nident2 = true\n}\n}\n\n";
			InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
			StreamTokenizer t = new StreamTokenizer(isr);
			t.commentChar('#');
			t.wordChars('_', '_');
			t.wordChars('-', '-');
			t.slashSlashComments(true);
			t.slashStarComments(true);
			ParserInternalTypeBase z = f.parseTokens("SECTION", null, t, "ROOT");
			assertAll("Expect result to have a \"section1\" containing a \"section2\" and an \"identifier\" and for \"section2\" to have \"ident2\"",
					() -> z.has("section1"), () -> z.get("section1").has("section2"), 
					() -> z.get("section1").has("identifier"), () -> z.get("section1").get("section2").has("ident2"));
		} catch(Exception e) {
			fail("Caught exception "+e.getMessage()+" when trying to instantiate a TypeFactory");
		}
	}
}
