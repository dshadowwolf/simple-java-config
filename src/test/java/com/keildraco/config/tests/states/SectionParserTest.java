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
import com.keildraco.config.states.IStateParser;
import com.keildraco.config.states.SectionParser;
import com.keildraco.config.types.*;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

@TestInstance(Lifecycle.PER_CLASS)
public class SectionParserTest {
	private TypeFactory factory;

	@BeforeAll
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
		this.factory.registerParser(() -> new SectionParser(this.factory, null, ""), "SECTION");
		this.factory.registerType((parent, name, value) -> new IdentifierType(parent, name, value), ItemType.IDENTIFIER);
		this.factory.registerType((parent, name, value) -> new ListType(parent, name, value), ItemType.LIST);
		this.factory.registerType((parent, name, value) -> new OperationType(parent, name, value), ItemType.OPERATION);
		this.factory.registerType((parent, name, value) -> new SectionType(parent, name, value), ItemType.SECTION);
	}

	@Test
	public final void testSectionParser() {
		try {
			@SuppressWarnings("unused")
			SectionParser p = new SectionParser(this.factory);
			assertTrue(true, "Expected no exception");
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}		
	}

	@Test
	public final void testSectionParserTypeFactorySectionTypeString() {
		try {
			@SuppressWarnings("unused")
			SectionParser p = new SectionParser(this.factory, null, "ROOT");
			assertTrue(true, "Expected no exception");
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}		
	}

	@Test
	public final void testSectionParserTypeFactoryString() {
		try {
			@SuppressWarnings("unused")
			SectionParser p = new SectionParser(this.factory, "ROOT");
			assertTrue(true, "Expected no exception");
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}		
	}
	
	@Test
	public final void testSetErrored() {
		try {
			SectionParser p = new SectionParser(this.factory, null, "ROOT");
			p.setErrored();
			assertTrue(true, "Expected no exception");
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}		
	}

	@Test
	public final void testErrored() {
		try {
			SectionParser p = new SectionParser(this.factory, null, "ROOT");
			assertFalse(p.errored(), "Expected fresh parser \"erorred()\" method to return false");
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}		
	}

	private ParserInternalTypeBase runParser(StreamTokenizer tok) {
		IStateParser k = this.factory.getParser("SECTION", null);
		k.setName("ROOT");
		ParserInternalTypeBase j = k.getState(tok);
		return j;
	}
	
	@Test
	public final void testGetState() {
		String testString = "section1 {\nidentifier = false\nsection2 {\nident2 = true\n}\n}\n\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase k = this.runParser(t);
		assertAll("Expecting the result to have \"section1\", \"section1\" to have \"section2\" and \"section2\" to have \"ident2\"",
				() -> k.has("section1"),  () -> k.get("section1").has("section2"),
				() -> k.get("section1").get("section2").has("ident2"));
	}

	@Test
	public final void testGetStateUnexpectedStore() {
		String testString = "section1 {\n= false\nidentifier = false\nsection2 {\nident2 = true\n}\n}\n\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase k = this.runParser(t);
		assertEquals(ParserInternalTypeBase.EmptyType, k, "Expecting to have k be EmptyType");
	}
	
	@Test
	public final void testGetStateUnexpectedItem() {
		String testString = "section1 { identifier(";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase k = this.runParser(t);
		assertEquals(ParserInternalTypeBase.EmptyType, k, "Expecting to have k be EmptyType");
	}
	
	@Test
	public final void testSetParent() {
		try {
			SectionParser p = new SectionParser(this.factory);
			p.setParent(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected setParent() to not have an exception");
		} catch( Exception e ) {
			fail("Caught exception in test: "+e.getMessage());
		}
	}

	@Test
	public final void testGetParent() {
		SectionParser p = new SectionParser(this.factory);
		assertNull(p.getParent(), "Fresh parser with not setParent() called returns null from getParent()");
	}

	@Test
	public final void testSetFactory() {
		try {
			SectionParser p = new SectionParser(this.factory);
			p.setFactory(Config.getFactory());
			assertTrue(true, "Expected setFactory() to not have an exception");
		} catch( Exception e ) {
			fail("Caught exception calling setFactory(): "+e.getMessage());
		}
	}
	
	@Test
	public final void testGetFactory() {
		try {
			SectionParser p = new SectionParser(this.factory);
			assertEquals(this.factory, p.getFactory());
		} catch( Exception e ) {
			fail("Caught exception calling getFactory(): "+e.getMessage());
		}
	}
	
	@Test
	public final void testGetName() {
		try {
			SectionParser p = new SectionParser(this.factory);
			assertEquals("ROOT", p.getName());
		} catch( Exception e ) {
			fail("Caught exception calling getName(): "+e.getMessage());
		}
	}
}
