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
import com.keildraco.config.states.IStateParser;
import com.keildraco.config.states.OperationParser;
import com.keildraco.config.states.SectionParser;
import com.keildraco.config.types.BooleanType;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.NumberType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.SectionType;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

public class OperationParserTest {
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
	            			tok.ttype != ']') System.err.println(String.format("<<<%c :: %s", tok.ttype<127?(tok.ttype>0?tok.ttype:'-'):'?', tok.sval));
	            	
	                return factory.getType(null, "", "", ParserInternalTypeBase.ItemType.LIST);
	            }
	        });
			
			when(p.getName()).thenAnswer(new Answer<String>() {
	 
	            public String answer(InvocationOnMock invocation) throws Throwable {
	                return "MockListType";
	            }
	        });
			return p;
		}, "LIST");
		this.factory.registerParser(() -> {
			IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class))).thenAnswer(new Answer<ParserInternalTypeBase>() {
	 
	            public ParserInternalTypeBase answer(InvocationOnMock invocation) throws Throwable {
	            	StreamTokenizer tok = (StreamTokenizer)invocation.getArgument(0);
	            	tok.nextToken();
	            	
	            	if(tok.ttype == StreamTokenizer.TT_WORD) return factory.getType(null, "", tok.sval, ParserInternalTypeBase.ItemType.IDENTIFIER);
	            	else if(tok.ttype == '[') return factory.parseTokens("LIST", null, tok, "");
	            	else return ParserInternalTypeBase.EmptyType;
	            }
	        });
			
			when(p.getName()).thenAnswer(new Answer<String>() {
	 
	            public String answer(InvocationOnMock invocation) throws Throwable {
	                return "MockIdentifierType";
	            }
	        });
			return p;
		}, "KEYVALUE");
		this.factory.registerParser(() -> {
			IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class))).thenAnswer(new Answer<ParserInternalTypeBase>() {
	 
	            public ParserInternalTypeBase answer(InvocationOnMock invocation) throws Throwable {
	            	StreamTokenizer tok = (StreamTokenizer)invocation.getArgument(0);
	            	while(tok.nextToken() != StreamTokenizer.TT_EOF &&
	            			tok.ttype != '}') System.err.println(String.format("<<<%c :: %s", tok.ttype<127?(tok.ttype>0?tok.ttype:'-'):'?', tok.sval));
	            	
	                return factory.getType(null, "", "", ParserInternalTypeBase.ItemType.SECTION);
	            }
	        });
			
			when(p.getName()).thenAnswer(new Answer<String>() {
	 
	            public String answer(InvocationOnMock invocation) throws Throwable {
	                return "MockSectionType";
	            }
	        });
			return p;
		}, "SECTION");
		this.factory.registerParser(() -> new OperationParser(factory), "OPERATION");
		this.factory.registerType((parent, name, value) -> new BooleanType(parent, name, value), ItemType.BOOLEAN);
		this.factory.registerType((parent, name, value) -> new IdentifierType(parent, name, value), ItemType.IDENTIFIER);
		this.factory.registerType((parent, name, value) -> new ListType(parent, name, value), ItemType.LIST);
		this.factory.registerType((parent, name, value) -> new NumberType(parent, name, value), ItemType.NUMBER);
		this.factory.registerType((parent, name, value) -> new OperationType(parent, name, value), ItemType.OPERATION);
		this.factory.registerType((parent, name, value) -> new SectionType(parent, name, value), ItemType.SECTION);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testOperationParserTypeFactory() {
		try {
			@SuppressWarnings("unused")
			OperationParser p = new OperationParser(this.factory);
			assertTrue("Expected no exception", true);
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}		
	}

	@Test
	public final void testOperationParserTypeFactoryParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			OperationParser p = new OperationParser(this.factory, null, "BUGGER");
			assertTrue("Expected no exception", true);
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}		
	}

	@Test
	public final void testSetFactory() {
		try {
			OperationParser p = new OperationParser(this.factory);
			p.setFactory(this.factory);
			assertTrue("Expected no exception", true);
		} catch( Exception e ) {
			fail("Caught exception using a parsers setFactory(TypeFactory) method: "+e.getMessage());
		}		
	}

	@Test
	public final void testGetFactory() {
		try {
			OperationParser p = new OperationParser(this.factory);
			TypeFactory f = p.getFactory();
			assertEquals(this.factory, f);
		} catch( Exception e ) {
			fail("Caught exception using a parsers getFactory() method: "+e.getMessage());
		}		
	}

	@Test
	public final void testSetErrored() {
		try {
			OperationParser p = new OperationParser(this.factory);
			p.setErrored();
			assertTrue("Expected no exception", true);
		} catch( Exception e ) {
			fail("Caught exception calling a parsers setErrored() method: "+e.getMessage());
		}		
	}

	@Test
	public final void testErrored() {
		try {
			OperationParser p = new OperationParser(this.factory);
			assertEquals(Boolean.FALSE, p.errored());
		} catch( Exception e ) {
			fail("Caught exception calling a parsers errored() method: "+e.getMessage());
		}		
	}

	@Test
	public final void testGetState() {
		String testString = "(! blargh)\n\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase k = this.factory.parseTokens("OPERATION", null, t, "blech");
		assertEquals("blech(! blargh)", k.asString().trim());
	}

	@Test
	public final void testSetParent() {
		try {
			OperationParser p = new OperationParser(this.factory);
			p.setParent(ParserInternalTypeBase.EmptyType);
			assertTrue("Expected no exception", true);
		} catch( Exception e ) {
			fail("Caught exception calling a parsers setParent() method: "+e.getMessage());
		}		
	}

	@Test
	public final void testGetParent() {
		try {
			OperationParser p = new OperationParser(this.factory);
			assertNull(p.getParent());
		} catch( Exception e ) {
			fail("Caught exception calling a parsers getParent() method: "+e.getMessage());
		}		
	}

	@Test
	public final void testGetName() {
		try {
			OperationParser p = new OperationParser(this.factory, null, "BUGGERED");
			assertEquals("BUGGERED", p.getName());
		} catch( Exception e ) {
			fail("Caught exception trying to get a parsers name: "+e.getMessage());
		}		
	}

	@Test
	public final void testClearErrors() {
		try {
			OperationParser p = new OperationParser(this.factory);
			p.clearErrors();
			assertTrue("Expected no exception", true);
		} catch( Exception e ) {
			fail("Caught exception telling a parser to clear its errors: "+e.getMessage());
		}		
	}

}
