package com.keildraco.config.tests;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

import com.keildraco.config.types.ParserInternalTypeBase.ItemType;
import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

import com.keildraco.config.data.DataQuery;
import com.keildraco.config.states.SectionParser;

public class ConfigAPITest {

	@Test
	public final void testRegisterType() {
		try {
			com.keildraco.config.Config.reset();
			com.keildraco.config.Config.registerType(ItemType.EMPTY, EmptyType.getClass());
		} catch(Exception e) {
			fail("Caught exception registering type: "+e.getMessage());
		} finally {
			assertTrue(true, "Able to register a type");
		}
	}

	@Test
	public final void testRegisterParser() {
		try {
			com.keildraco.config.Config.reset();
			com.keildraco.config.Config.registerParser("SECTION", SectionParser.class);
		} catch(Exception e) {
			fail("Caught exception registering parser: "+e.getMessage());
		} finally {
			assertTrue(true, "Able to register a parser");
		}
	}

	@Test
	public final void testRegisterKnownParts() {
		try {
			com.keildraco.config.Config.reset();
			com.keildraco.config.Config.registerKnownParts();
		} catch(Exception e) {
			fail("Caught exception calling Config.registerKnownParts(): "+e.getMessage());
		} finally {
			assertTrue(true, "Able to register known types and parsers");
		}
	}

	@Test
	public final void testLoadFileURI() {
		DataQuery c = null;
		try {
			com.keildraco.config.Config.reset();
			com.keildraco.config.Config.registerKnownParts();
			c = com.keildraco.config.Config.LoadFile(Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg").toUri());
			assertNotNull(c, "Load Worked? ");
		} catch (IOException | IllegalArgumentException e ) {
			fail(String.format("Caught exception running LoadFile([URI] %s)\n---> %s", Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg").toUri(), e));
		}
	}

	@Test
	public final void testLoadFilePath() {
		Path p = Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg");
		DataQuery c = null;
		try {
			com.keildraco.config.Config.reset();
			com.keildraco.config.Config.registerKnownParts();
			c = com.keildraco.config.Config.LoadFile(p);
			assertTrue(c != null, "Load Worked? ");
		} catch (IOException | IllegalArgumentException e ) {
			fail(String.format("Caught exception running LoadFile([PATH] %s)\n---> %s", Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg").toString(), e));
		}
	}

	@Test
	public final void testLoadFileString() {
		DataQuery c = null;
		
		try {
			com.keildraco.config.Config.reset();
			com.keildraco.config.Config.registerKnownParts();
			c = com.keildraco.config.Config.LoadFile(Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg").toString());
			assertTrue(c != null, "Load Worked? ");
		} catch (IOException | IllegalArgumentException e ) {
			fail(String.format("Caught exception running LoadFile([STRING] %s)\n---> %s", Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg").toString(), e));
		}
		
	}

	@Test
	public final void testParseString() {
		com.keildraco.config.Config.reset();
		com.keildraco.config.Config.registerKnownParts();
		DataQuery c = com.keildraco.config.Config.parseString("section {\n key = [ ident1, ident2, ident3(! ident4)\n}\n\n");
		assertTrue(c != null, String.format("Load worked as expected (%s)", c));
	}

}
