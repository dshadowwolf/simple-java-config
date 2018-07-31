package com.keildraco.config.tests;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.StreamTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Enumeration;

import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.DataQuery;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.IStateParser;
import com.keildraco.config.states.SectionParser;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

public class ConfigAPITest {

	@Test
	public final void testRegisterType() {
		try {
			Config.reset();
			Config.registerType(ItemType.EMPTY, EmptyType.getClass());
		} catch (final Exception e) {
			fail("Caught exception registering type: " + e.getMessage());
		} finally {
			assertTrue(true, "Able to register a type");
		}
	}

	@Test
	public final void testRegisterParser() {
		try {
			Config.reset();
			Config.registerParser("SECTION", SectionParser.class);
		} catch (final Exception e) {
			fail("Caught exception registering parser: " + e.getMessage());
		} finally {
			assertTrue(true, "Able to register a parser");
		}
	}

	@Test
	public final void testRegisterKnownParts() {
		try {
			Config.reset();
			Config.registerKnownParts();
		} catch (final Exception e) {
			fail("Caught exception calling Config.registerKnownParts(): " + e.getMessage());
		} finally {
			assertTrue(true, "Able to register known types and parsers");
		}
	}

	@Test
	public final void testLoadFileURI() {
		DataQuery c = null;
		try {
			Config.reset();
			Config.registerKnownParts();
			URI t = Config.class.getClassLoader().getResource("assets/base-config-test.cfg").toURI();
			c = Config.loadFile(t);
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | URISyntaxException e) {
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	public final void testLoadFilePath() {
		Path p = Paths.get("assets", "base-config-test.cfg");
		DataQuery c = null;
		try {
			Config.reset();
			Config.registerKnownParts();
			c = com.keildraco.config.Config.loadFile(p);
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | URISyntaxException e) {
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	public final void testLoadFileString() {
		DataQuery c = null;

		try {
			Config.reset();
			Config.registerKnownParts();
			Path p = Paths.get("assets", "base-config-test.cfg");
			String fp = String.join("/", p.toString().split("\\\\"));
			c = com.keildraco.config.Config.loadFile(fp);
			assertNotNull(c, "Load Worked? ");
		} catch (final IOException | IllegalArgumentException | URISyntaxException e) {
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	public final void testParseString() {
		Config.reset();
		Config.registerKnownParts();
		final DataQuery c = com.keildraco.config.Config
				.parseString("section {\n key = [ ident1, ident2, ident3(! ident4)\n}\n\n");
		assertNotNull(c, String.format("Load worked as expected (%s)", c));
	}

	@Test
	public final void testRegisterParserError() {
		try {
			final Method m = Config.class.getDeclaredMethod("registerParserGenerator", String.class,
					Class.class);
			m.setAccessible(true);
			final Object p = m.invoke(Config.INSTANCE, "BROKEN", BrokenParser.class);
			assertNull(p, "broken parser should return null from generator");
		} catch (final NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			Config.LOGGER.fatal("Exception: %s\nMessage: %s\nStack Dump:", e, e.getMessage());
			for (final StackTraceElement elem : e.getStackTrace()) {
				Config.LOGGER.fatal("%s", elem);
			}
			fail("test broken, exception caught: " + e.getMessage());
		}
	}

	@Test
	public final void testRegisterTypeError() {
		try {
			final Method m = Config.class.getDeclaredMethod("registerTypeGenerator",
					ParserInternalTypeBase.class, String.class, String.class, Class.class);
			m.setAccessible(true);
			final Object p = m.invoke(Config.INSTANCE, ParserInternalTypeBase.EmptyType, "BROKEN",
					"BROKEN", BrokenType.class);
			assertNull(p, "broken type should return null from generator");
		} catch (final NoSuchMethodException | SecurityException | IllegalAccessException
				| IllegalArgumentException | InvocationTargetException e) {
			Config.LOGGER.fatal("Exception: %s\nMessage: %s\nStack Dump:", e, e.getMessage());
			for (final StackTraceElement elem : e.getStackTrace()) {
				Config.LOGGER.fatal("%s", elem);
			}
			fail("test broken, exception caught: " + e.getMessage());
		}
	}
	
	protected class BrokenType extends ParserInternalTypeBase {

		public BrokenType(final ParserInternalTypeBase parent, final String name) {
			super(parent, name);
		}
	}

	protected class BrokenParser implements IStateParser {

		public BrokenParser(final String blargh) {
		}

		@SuppressWarnings("unused")
		private BrokenParser(final TypeFactory factory) {
		}

		@Override
		public void setFactory(final TypeFactory factory) {
			// blank, not needed
		}

		@Override
		public TypeFactory getFactory() {
			return null;
		}

		@Override
		public void setErrored() {
			// blank, not needed
		}

		@Override
		public boolean errored() {
			return false;
		}

		@Override
		public ParserInternalTypeBase getState(final StreamTokenizer tok) {
			return null;
		}

		@Override
		public void setParent(final ParserInternalTypeBase parent) {
			// blank, not needed
		}

		@Override
		public ParserInternalTypeBase getParent() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}

		@Override
		public void clearErrors() {
			// blank, not needed
		}
	}
}
