package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.*;

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

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.Tokenizer;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.factory.Tokenizer.TokenType;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.IdentifierType;

class AbstractParserBaseTest {
	@Test
	final void testAbstractParserBase() {
		try {
			TypeFactory f = new TypeFactory();
			AbstractParserBase apb = new AbstractParserBase(f,null,"BLARGH") { 
				@Override
				public void registerTransitions(TypeFactory factory) {
					// intentionally blank
				};
			};
			assertTrue(apb!=null);
		} catch(Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testSetFactory() {
		try {
			TypeFactory f = new TypeFactory();
			AbstractParserBase apb = new AbstractParserBase(null,null,"BLARGH") { 
				@Override
				public void registerTransitions(TypeFactory factory) {
					// intentionally blank
				};
			};
			apb.setFactory(f);
			assertTrue(true, "AbstractParserBase.setFactory() works");
		} catch(Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testGetFactory() {
		try {
			TypeFactory f = new TypeFactory();
			AbstractParserBase apb = new AbstractParserBase(f,null,"BLARGH") { 
				@Override
				public void registerTransitions(TypeFactory factory) {
					// intentionally blank
				};
			};
			assertEquals(f, apb.getFactory());
		} catch(Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testSetParent() {
		try {
			TypeFactory f = new TypeFactory();
			AbstractParserBase apb = new AbstractParserBase(f,null,"BLARGH") { 
				@Override
				public void registerTransitions(TypeFactory factory) {
					// intentionally blank
				};
			};
			apb.setParent(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "setParent() works");
		} catch(Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testGetParent() {
		try {
			TypeFactory f = new TypeFactory();
			AbstractParserBase apb = new AbstractParserBase(f,null,"BLARGH") { 
				@Override
				public void registerTransitions(TypeFactory factory) {
					// intentionally blank
				};
			};
			IdentifierType it = new IdentifierType("test");
			apb.setParent(it);
			assertEquals(it, apb.getParent());
		} catch(Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testGetName() {
		try {
			TypeFactory f = new TypeFactory();
			AbstractParserBase apb = new AbstractParserBase(f,null,"BLARGH") { 
				@Override
				public void registerTransitions(TypeFactory factory) {
					// intentionally blank
				};
			};
			assertEquals("BLARGH", apb.getName());
		} catch(Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testSetName() {
		try {
			TypeFactory f = new TypeFactory();
			AbstractParserBase apb = new AbstractParserBase(f,null,"BLARGH") { 
				@Override
				public void registerTransitions(TypeFactory factory) {
					// intentionally blank
				};
			};
			apb.setName("BLECH");
			assertEquals("BLECH", apb.getName());
		} catch(Exception e) {
			Config.LOGGER.error("Exception getting instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	private ParserInternalTypeBase doParse(AbstractParserBase parser, String data) throws IOException, IllegalParserStateException, UnknownStateException, GenericParseException, NoSuchMethodException, InstantiationException, IllegalAccessException, InvocationTargetException {
		InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
		InputStreamReader br = new InputStreamReader(is);
		StreamTokenizer tok = new StreamTokenizer(br);
		Tokenizer t = new Tokenizer(tok);
		Config.LOGGER.fatal("parser: %s%nis: %s%nbr: %s%ntok: %s%nt: %s%n", parser, is, br, tok, t);
		return parser.getState(t);
	}

	@Test
	final void testGetState() {
		try {
			Config.registerKnownParts();
			Path p = Paths.get("assets", "base-config-test.cfg");
			String ts = String.join("/", p.toString().split("\\\\"));
			URL tu = Config.class.getClassLoader().getResource(ts);
			URI temp = tu.toURI();
			InputStream is = temp.toURL().openStream();
			InputStreamReader br = new InputStreamReader(is);
			StreamTokenizer tok = new StreamTokenizer(br);
			Tokenizer t = new Tokenizer(tok);
			AbstractParserBase apb = new AbstractParserBase(Config.getFactory(),null,"BLARGH") { 
				@Override
				public void registerTransitions(TypeFactory factory) {
					factory.registerStateTransition(this.getName().toUpperCase(), TokenType.IDENTIFIER, TokenType.OPEN_BRACE, "SECTION");
					factory.registerStateTransition(this.getName().toUpperCase(), TokenType.IDENTIFIER, TokenType.STORE, "KEYVALUE");
				};
			};
			Config.getFactory().registerParser(() -> apb, "BLARGH");
			apb.registerTransitions(Config.getFactory());
			ParserInternalTypeBase res = apb.getState(t);
			assertAll( () -> assertTrue(res!=ParserInternalTypeBase.EmptyType, "AbstractParserBase.getState() works"),
					() -> assertThrows(IllegalParserStateException.class, () -> doParse(apb, ""), "throws on null input"),
					() -> assertEquals(ParserInternalTypeBase.EmptyType, doParse(apb, "alpha(!bravo)"), "returns ParserInternalTypeBase.EmptyType on an interally caught exception"));
		} catch (final IOException | IllegalArgumentException | URISyntaxException | IllegalParserStateException | UnknownStateException | GenericParseException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

}
