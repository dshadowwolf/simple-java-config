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
			final IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class))).thenAnswer(new Answer<ParserInternalTypeBase>() {

	            public ParserInternalTypeBase answer(final InvocationOnMock invocation) throws Throwable {
	            	final StreamTokenizer tok = (StreamTokenizer) invocation.getArgument(0);
	            	while (tok.nextToken() != StreamTokenizer.TT_EOF &&
	            			tok.ttype != ']') System.err.println(String.format("<<<%c :: %s", tok.ttype < 127 ? (tok.ttype > 0 ? tok.ttype:'-'):'?', tok.sval));

	                return factory.getType(null, "", "", ParserInternalTypeBase.ItemType.LIST);
	            }
	        });

			when(p.getName()).thenAnswer(new Answer<String>() {

	            public String answer(final InvocationOnMock invocation) throws Throwable {
	                return "MockListType";
	            }
	        });
			return p;
		}, "LIST");
		this.factory.registerParser(() -> {
			final IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class))).thenAnswer(new Answer<ParserInternalTypeBase>() {

	            public ParserInternalTypeBase answer(final InvocationOnMock invocation) throws Throwable {
	            	final StreamTokenizer tok = (StreamTokenizer) invocation.getArgument(0);
	            	tok.nextToken();

	            	if (tok.ttype == StreamTokenizer.TT_WORD) {
	            		return factory.getType(null, "", tok.sval, ParserInternalTypeBase.ItemType.IDENTIFIER);
	            	} else if (tok.ttype == '[') { return factory.parseTokens("LIST", null, tok, "");
	            	} else {
	            		return ParserInternalTypeBase.EmptyType;
	            	}
	            }
	        });

			when(p.getName()).thenAnswer(new Answer<String>() {

	            public String answer(final InvocationOnMock invocation) throws Throwable {
	                return "MockIdentifierType";
	            }
	        });
			return p;
		}, "KEYVALUE");
		this.factory.registerParser(() -> {
			final IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class))).thenAnswer(new Answer<ParserInternalTypeBase>() {

	            public ParserInternalTypeBase answer(final InvocationOnMock invocation) throws Throwable {
	            	final StreamTokenizer tok = (StreamTokenizer) invocation.getArgument(0);
	            	while (tok.nextToken() != StreamTokenizer.TT_EOF &&
	            			tok.ttype != ')');

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
			final SectionParser p = new SectionParser(this.factory);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testSectionParserTypeFactorySectionTypeString() {
		try {
			@SuppressWarnings("unused")
			final SectionParser p = new SectionParser(this.factory, null, "ROOT");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	@Test
	public final void testSectionParserTypeFactoryString() {
		try {
			@SuppressWarnings("unused")
			final SectionParser p = new SectionParser(this.factory, "ROOT");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: "+e.getMessage());
		}
	}

	private ParserInternalTypeBase runParser(final StreamTokenizer tok) {
		final IStateParser k = this.factory.getParser("SECTION", null);
		k.setName("ROOT");
		final ParserInternalTypeBase j = k.getState(tok);
		return j;
	}

	@Test
	public final void testGetState() {
		final String testString = "section1 {\nidentifier = false\nsection2 {\nident2 = true\n}\n}\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = this.runParser(t);
		assertAll("Expecting the result to have \"section1\", \"section1\" to have \"section2\" and \"section2\" to have \"ident2\"",
				() -> k.has("section1"),  () -> k.get("section1").has("section2"),
				() -> k.get("section1").get("section2").has("ident2"));
	}

	@Test
	public final void testGetStateUnexpectedStore() {
		final String testString = "section1 {\n= false\nidentifier = false\nsection2 {\nident2 = true\n}\n}\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = this.runParser(t);
		assertEquals(ParserInternalTypeBase.EmptyType, k, "Expecting to have k be EmptyType");
	}

	@Test
	public final void testGetStateUnexpectedItem() {
		final String testString = "section1 { identifier(";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = this.runParser(t);
		assertEquals(ParserInternalTypeBase.EmptyType, k, "Expecting to have k be EmptyType");
	}
}
