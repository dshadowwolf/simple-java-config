package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.IStateParser;
import com.keildraco.config.states.KeyValueParser;
import com.keildraco.config.types.*;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

@TestInstance(Lifecycle.PER_CLASS)
public class KeyValueParserTest {
	private TypeFactory factory;
	
	@BeforeAll
	public void setUp() throws Exception {
		
		this.factory = new TypeFactory();
		this.factory.registerParser(() -> {
			IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class))).thenAnswer(new Answer<ParserInternalTypeBase>() {
	 
	            public ParserInternalTypeBase answer(InvocationOnMock invocation) throws Throwable {
	            	while(((StreamTokenizer)invocation.getArgument(0)).nextToken() != StreamTokenizer.TT_EOF &&
	            			((StreamTokenizer)invocation.getArgument(0)).ttype != ']') ;
	            	
	            	if(((StreamTokenizer)invocation.getArgument(0)).ttype == ']') ((StreamTokenizer)invocation.getArgument(0)).nextToken();
	                return new ListType(null, "", "");
	            }
	        });
			
			when(p.getName()).thenAnswer(new Answer<String>() {
	 
	            public String answer(InvocationOnMock invocation) throws Throwable {
	                return "MockType";
	            }
	        });
			return p;
		}, "LIST");
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
		this.factory.registerParser(() -> new KeyValueParser(this.factory, "KEYVALUE"), "KEYVALUE");
		this.factory.registerType((parent, name, value) -> new IdentifierType(parent, name, value), ItemType.IDENTIFIER);
		this.factory.registerType((parent, name, value) -> new ListType(parent, name, value), ItemType.LIST);
		this.factory.registerType((parent, name, value) -> new OperationType(parent, name, value), ItemType.OPERATION);
		this.factory.registerType((parent, name, value) -> new SectionType(parent, name, value), ItemType.SECTION);
	}

	@Test
	public final void testKeyValueParser() {
		try {
			@SuppressWarnings("unused")
			KeyValueParser p = new KeyValueParser(this.factory, "KEYVALUE");
			assertTrue(true, "Expected no exception");
		} catch( Exception e ) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testSetErrored() {
		try {
			KeyValueParser p = new KeyValueParser(this.factory, "KEYVALUE");
			p.setErrored();
			assertTrue(true, "Expected no exception");
		} catch( Exception e ) {
			fail("Caught exception calling p.setErrored(): "+e.getMessage());
		}
	}

	@Test
	public final void testErrored() {
		KeyValueParser p = new KeyValueParser(this.factory, "KEYVALUE");
		assertFalse(p.errored(), "Expected p.errored() to return false");
	}

	@Test
	public final void testGetState() {
		String testString = "[ a_value, another_value, a_third_value ]\n\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase k = this.factory.parseTokens("KEYVALUE", null, t, "a_key");
		assertEquals("a_key = [  ]", k.asString());
	}

	@Test
	public final void testSetParent() {
		try {
			KeyValueParser p = new KeyValueParser(this.factory, "KEYVALUE");
			p.setParent(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected no exception");
		} catch( Exception e ) {
			fail("Caught exception calling p.setParent(ParserInternalTypeBase.EmptyType): "+e.getMessage());
		}		
	}

	@Test
	public final void testGetParent() {
		KeyValueParser p = new KeyValueParser(this.factory, "KEYVALUE");
		assertNull(p.getParent(), "Expected p.getParent() to return null");
	}

	@Test
	public final void testGetName() {
		KeyValueParser p = new KeyValueParser(this.factory, "KEYVALUE");
		assertEquals("KEYVALUE", p.getName());
	}
	
	@Test
	public final void testGetFactory() {
		KeyValueParser p = new KeyValueParser(this.factory, "KEYVALUE");
		assertEquals(this.factory, p.getFactory());
	}

	@Test
	public final void testSetFactory() {
		try {
			KeyValueParser p = new KeyValueParser(this.factory, "KEYVALUE");
			p.setFactory(Config.getFactory());
			assertTrue(true, "Expected no exception");
		} catch( Exception e ) {
			fail("Caught exception calling p.setFactory(Config.getFactory()): "+e.getMessage());
		}		
	}
	
	@Test
	public final void testEarlyEOF() {
		Config.reset();
		Config.registerKnownParts();
		String testString = "an_ident = \n\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase k = Config.getFactory().parseTokens("SECTION", null, t, "blargh");
		assertEquals(ParserInternalTypeBase.EmptyType, k, "expect EmptyType due to malformation");
	}

	@Test
	public final void testEarlySectionEnd() {
		Config.reset();
		Config.registerKnownParts();
		String testString = "an_ident = }\n\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase k = Config.getFactory().parseTokens("SECTION", null, t, "blargh");
		assertEquals(ParserInternalTypeBase.EmptyType, k, "expect EmptyType due to malformation");
	}

	@Test
	public final void testNonWordWhereIdentExpected() {
		Config.reset();
		Config.registerKnownParts();
		String testString = "an_ident = ;\n\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase k = Config.getFactory().parseTokens("SECTION", null, t, "blargh");
		assertEquals(ParserInternalTypeBase.EmptyType, k, "expect EmptyType due to malformation");
	}

	@Test
	public final void testBadFormat() {
		Config.reset();
		Config.registerKnownParts();
		String testString = "an_ident = other\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		IStateParser p = Config.getFactory().getParser("KEYVALUE", null);
		p.setErrored();
		ParserInternalTypeBase k = p.getState(t);
		assertEquals(ParserInternalTypeBase.EmptyType, k, "expect EmptyType due to malformation");
	}
}
