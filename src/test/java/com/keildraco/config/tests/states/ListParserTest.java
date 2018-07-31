package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.*;
import com.keildraco.config.types.*;

import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;

@TestInstance(Lifecycle.PER_CLASS)
public class ListParserTest {
	private TypeFactory factory;
	
	@BeforeAll
	public void setUp() throws Exception {
		this.factory = new TypeFactory();
		this.factory.registerParser(() -> {
			final IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class))).thenAnswer(new Answer<ParserInternalTypeBase>() {
	 
	            public ParserInternalTypeBase answer(final InvocationOnMock invocation) throws Throwable {
	            	StreamTokenizer tok = (StreamTokenizer) invocation.getArgument(0);
	            	while (tok.nextToken() != StreamTokenizer.TT_EOF &&
	            			tok.ttype != ')') ;
	            	
	                return factory.getType(null, "", "", ItemType.OPERATION);
	            }
	        });
			
			when(p.getName()).thenAnswer(new Answer<String>() {
	 
	            public String answer(final InvocationOnMock invocation) throws Throwable {
	                return "MockOperationType";
	            }
	        });
			return p;
		}, "OPERATION");
		this.factory.registerParser(() -> new ListParser(this.factory, "LIST"), "LIST");
		this.factory.registerType((parent, name, value) -> new IdentifierType(parent, name, value), ItemType.IDENTIFIER);
		this.factory.registerType((parent, name, value) -> new ListType(parent, name, value), ItemType.LIST);
		this.factory.registerType((parent, name, value) -> new OperationType(parent, name, value), ItemType.OPERATION);
		this.factory.registerType((parent, name, value) -> new SectionType(parent, name, value), ItemType.SECTION);
	}

	@Test
	public final void testListParser() {
		try {
			@SuppressWarnings("unused")
			final ListParser p = new ListParser(this.factory, "LIST");
			assertTrue(true, "Expected to not get an exception");
		} catch(final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testSetErrored() {
		try {
			final ListParser p = new ListParser(this.factory, "LIST");
			p.setErrored();
			assertTrue(true, "Expected to not get an exception");
		} catch(final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testErrored() {
		try {
			final ListParser p = new ListParser(this.factory, "LIST");
			assertFalse(p.errored(), "Expected new parser instance to return false from the errored() method");
		} catch(final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testGetState() {
		final String testString = "a_value, an_operator(!ident), false ]\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = this.factory.parseTokens("LIST", null, t, "");
		assertEquals("[ a_value, an_operator(null ), false ]", k.asString());
	}

	@Test
	public final void testSetParent() {
		try {
			final ListParser p = new ListParser(this.factory, "LIST");
			p.setParent(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected setParent() to not have an exception");
		} catch(final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testGetParent() {
		final ListParser p = new ListParser(this.factory, "LIST");
		assertNull(p.getParent(), "Expected getParent() on a fresh parser to be null");
	}

	@Test
	public final void testGetStateErrorOne() {
		final String testString = "[ a_value, an_operator(!ident), fa-lse ]\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.wordChars('0', '9');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = this.factory.parseTokens("LIST", null, t, "");
		assertEquals(ParserInternalTypeBase.EmptyType, k, "k should be EmptyType due to bad format of input");
	}

	@Test
	public final void testGetStateErrorTwo() {
		final String testString = "[ a_value, an_operator(!ident), false true ]\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.wordChars('0', '9');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = this.factory.parseTokens("LIST", null, t, "");
		assertEquals(ParserInternalTypeBase.EmptyType, k, "k should be EmptyType due to bad format of input");
	}

	@Test
	public final void testSetFactory() {
		try {
			final ListParser p = new ListParser(this.factory, "LIST");
			p.setFactory(Config.getFactory());
			assertTrue(true, "Expected setFactory() to not have an exception");
		} catch(final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testGetFactory() {
		final ListParser p = new ListParser(this.factory, "LIST");
		assertEquals(this.factory, p.getFactory(), "p.getFactory() should equal the factory for the test suite");
	}
}
