package com.keildraco.config.tests;

import static org.junit.Assert.*;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.types.ParserInternalTypeBase.ItemType;
import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;

import com.keildraco.config.data.DataQuery;
import com.keildraco.config.states.SectionParser;

public class ConfigAPITest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testRegisterType() {
		try {
			com.keildraco.config.Config.reset();
			com.keildraco.config.Config.registerType(ItemType.EMPTY, EmptyType.getClass());
		} catch(Exception e) {
			fail("Caught exception registering type: "+e.getMessage());
		} finally {
			assertTrue("Able to register a type", true);
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
			assertTrue("Able to register a parser", true);
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
			assertTrue("Able to register known types and parsers", true);
		}
	}

	@Test
	public final void testLoadFileURI() {
		DataQuery c = null;
		try {
			com.keildraco.config.Config.reset();
			com.keildraco.config.Config.registerKnownParts();
			c = com.keildraco.config.Config.LoadFile(Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg").toUri());
			assertTrue("Load Worked? ", c != null);
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
			
			assertTrue("Load Worked? ", c != null);
		} catch (IOException | URISyntaxException | IllegalArgumentException e ) {
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
			assertTrue("Load Worked? ", c != null);
		} catch (IOException | URISyntaxException | IllegalArgumentException e ) {
			fail(String.format("Caught exception running LoadFile([STRING] %s)\n---> %s", Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg").toString(), e));
		}
		
	}

	@Test
	public final void testParseString() {
		com.keildraco.config.Config.reset();
		com.keildraco.config.Config.registerKnownParts();
		DataQuery c = com.keildraco.config.Config.parseString("section {\n key = [ ident1, ident2, ident3(! ident4)\n}\n\n");
		assertTrue(String.format("Load worked as expected (%s)", c), c != null);
	}

}
