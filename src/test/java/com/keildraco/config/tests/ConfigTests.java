package com.keildraco.config.tests;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.DataQuery;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;

class ConfigTests {

	@Test
	final void testGetFactory() {
		assertNotNull(Config.getFactory());
	}

	/*
	 * There should be a "testRegisterType" and "testRegisterParser" here, but those are covered by
	 * "testRegisterKnownParts()"
	 */
	
	/**
	 * Test the automatic (using reflection) registration of known value types and parser states and state transitions
	 * 
	 * Will fail if it catches any of a number of possible exceptions
	 */
	@Test
	final void testRegisterKnownParts() {
		try {
			Config.registerKnownParts();
			assertTrue(true, "able to register known bits automatically and without exceptions");
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			Config.LOGGER.fatal("Exception %s", e.toString());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::fatal);
			fail("Exception Caught");
		}
	}

	@Test
	final void testReset() {
		try {
			Config.registerKnownParts();
			IStateParser p = Config.getFactory().getParser("SECTION", null);
			Config.reset();
			IStateParser q = Config.getFactory().getParser("SECTION", null);
			assertAll("parser prior to reset should not equal a parser post reset", () -> assertNull(q), () -> assertTrue(p!=null), () -> assertFalse(p==q) );
		} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			Config.LOGGER.fatal("Exception %s", e.toString());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::fatal);
			fail("Exception Caught");
		}
	}

	/*
	 * There would be a testParseStream() here, but it is actually called from all the various parse() functions.
	 * Same for testLoadFileURI(), actually, so neither gets a separate test. loadFileString() and loadFilePath() both
	 * call loadFileURI() - which calls parseStream().
	 */

	@Test
	public final void testLoadFilePath() {
		Path p = Paths.get("assets", "base-config-test.cfg");
		DataQuery c = null;
		try {
			Config.reset();
			Config.registerKnownParts();
			c = com.keildraco.config.Config.loadFile(p);
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | URISyntaxException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testLoadFileString() {
		DataQuery c = null;
		try {
			Config.reset();
			Config.registerKnownParts();
			c = com.keildraco.config.Config.loadFile("assets/base-config-test.cfg");
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | URISyntaxException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testParseString() {
		DataQuery c = null;
		try {
			Config.reset();
			Config.registerKnownParts();
			c = com.keildraco.config.Config.parseString("section { item = value\n item2 = [ list, op(! ident), op2(~ident2) ]\nsubsection { item3 = ident3 } }");
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException | IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testErrorStates() {
		Config.registerParser("WILLTHROW", ParserThatThrows.class);
		Config.registerType(ItemType.INVALID, TypeThatThrows.class);
		
		IStateParser p = Config.getFactory().getParser("WILLTHROW", null);
		ParserInternalTypeBase t = Config.getFactory().getType(null, "", "", ItemType.INVALID);
		assertAll(() -> assertNull(p), () -> assertNull(t));
	}
	
	private class ParserThatThrows extends AbstractParserBase implements IStateParser {

		public ParserThatThrows(TypeFactory f, ParserInternalTypeBase b) throws IllegalAccessException {
			super(f,b, "TEST");
			throw new IllegalAccessException("testing purposes only");
		}
		
		@Override
		public void registerTransitions(TypeFactory factory) {
			// not needed
		}
	}
	
	private class TypeThatThrows extends ParserInternalTypeBase {

		public TypeThatThrows(ParserInternalTypeBase parentIn, String nameIn, String valueIn) throws IllegalAccessException {
			super(parentIn, nameIn, valueIn);
			throw new IllegalAccessException("testing purposes only");
		}
		
	}
}
