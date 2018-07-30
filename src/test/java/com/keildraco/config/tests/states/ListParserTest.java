package com.keildraco.config.tests.states;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.*;
import com.keildraco.config.types.*;
import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;

public class ListParserTest {
	private TypeFactory factory;
	
	@Before
	public void setUp() throws Exception {
		this.factory = new TypeFactory();
		this.factory.registerParser(() -> {
			IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class))).thenAnswer(new Answer<ParserInternalTypeBase>() {
	 
	            public ParserInternalTypeBase answer(InvocationOnMock invocation) throws Throwable {
	            	StreamTokenizer tok = (StreamTokenizer)invocation.getArgument(0);
	            	while(tok.nextToken() != StreamTokenizer.TT_EOF &&
	            			tok.ttype != ')') ;
	            	
	                return factory.getType(null, "", "", ItemType.OPERATION);
	            }
	        });
			
			when(p.getName()).thenAnswer(new Answer<String>() {
	 
	            public String answer(InvocationOnMock invocation) throws Throwable {
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

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testListParser() {
		try {
			@SuppressWarnings("unused")
			ListParser p = new ListParser(this.factory, "LIST");
			assertTrue("Expected to not get an exception", true);
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testSetErrored() {
		try {
			ListParser p = new ListParser(this.factory, "LIST");
			p.setErrored();
			assertTrue("Expected to not get an exception", true);
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testErrored() {
		try {
			ListParser p = new ListParser(this.factory, "LIST");
			assertTrue("Expected new parser instance to return false from the errored() method", p.errored()==false);
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testGetState() {
		String testString = "a_value, an_operator(!ident), false ]\n\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase k = this.factory.parseTokens("LIST", null, t, "");
		assertEquals("[ a_value, an_operator(null ), false ]", k.asString());
	}

	@Test
	public final void testSetParent() {
		try {
			ListParser p = new ListParser(this.factory, "LIST");
			p.setParent(ParserInternalTypeBase.EmptyType);
			assertTrue("Expected setParent() to not have an exception", true);
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testGetParent() {
		ListParser p = new ListParser(this.factory, "LIST");
		assertTrue("Expected getParent() on a fresh parser to be null", p.getParent()==null);
	}

}
