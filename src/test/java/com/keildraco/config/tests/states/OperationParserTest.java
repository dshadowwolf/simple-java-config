package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.IStateParser;
import com.keildraco.config.states.OperationParser;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;
import com.keildraco.config.types.SectionType;

@TestInstance(Lifecycle.PER_CLASS)
public class OperationParserTest {

	private TypeFactory factory;

	@BeforeAll
	public void setUp() throws Exception {
		this.factory = new TypeFactory();
		this.factory.registerParser(() -> {
			IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class))).thenAnswer(new Answer<ParserInternalTypeBase>() {

	            public ParserInternalTypeBase answer(final InvocationOnMock invocation) throws Throwable {
	            	final StreamTokenizer tok = (StreamTokenizer) invocation.getArgument(0);
	            	while (tok.nextToken() != StreamTokenizer.TT_EOF
	            			&& tok.ttype != ']') System.err.println(String.format("<<<%c :: %s", tok.ttype < 127 ? (tok.ttype > 0 ? tok.ttype : '-') : '?', tok.sval));

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
	            	} else if (tok.ttype == '[') {
	            		return factory.parseTokens("LIST", null, tok, "");
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
	            	while (tok.nextToken() != StreamTokenizer.TT_EOF
	            			&& tok.ttype != '}') System.err.println(String.format("<<<%c :: %s", tok.ttype < 127 ? (tok.ttype > 0 ? tok.ttype : '-') : '?', tok.sval));

	                return factory.getType(null, "", "", ParserInternalTypeBase.ItemType.SECTION);
	            }
	        });

			when(p.getName()).thenAnswer(new Answer<String>() {

	            public String answer(final InvocationOnMock invocation) throws Throwable {
	                return "MockSectionType";
	            }
	        });
			return p;
		}, "SECTION");
		this.factory.registerParser(() -> new OperationParser(factory), "OPERATION");
		this.factory.registerType((parent, name, value) -> new IdentifierType(parent, name, value), ItemType.IDENTIFIER);
		this.factory.registerType((parent, name, value) -> new ListType(parent, name, value), ItemType.LIST);
		this.factory.registerType((parent, name, value) -> new OperationType(parent, name, value), ItemType.OPERATION);
		this.factory.registerType((parent, name, value) -> new SectionType(parent, name, value), ItemType.SECTION);
	}

	@Test
	public final void testOperationParserTypeFactory() {
		try {
			@SuppressWarnings("unused")
			final OperationParser p = new OperationParser(this.factory);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: " + e.getMessage());
		}
	}

	@Test
	public final void testOperationParserTypeFactoryParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			final OperationParser p = new OperationParser(this.factory, null, "BUGGER");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: " + e.getMessage());
		}
	}

	@Test
	public final void testGetState() {
		final String testString = "(! blargh)\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = this.factory.parseTokens("OPERATION", null, t, "blech");
		assertEquals("blech(! blargh)", k.asString().trim());
	}

	@Test
	public final void testBadParseNoClose() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "(! ident\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		ParserInternalTypeBase testItem = Config.getFactory().parseTokens("OPERATION", null, t, "op");
		assertEquals(ParserInternalTypeBase.EmptyType, testItem, "expect failed parse to return EmptyType");
	}

	@Test
	public final void testBadParseNoOper() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "(ident\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase testItem = Config.getFactory().parseTokens("OPERATION", null, t, "op");
		assertEquals(ParserInternalTypeBase.EmptyType, testItem, "expect failed parse to return EmptyType");
	}

	@Test
	public final void testBadParseNoIdent() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "(~\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase testItem = Config.getFactory().parseTokens("OPERATION", null, t, "op");
		assertEquals(ParserInternalTypeBase.EmptyType, testItem, "expect failed parse to return EmptyType");
	}

	@Test
	public final void testParseStartsAfterParens() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "! ident)\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase testItem = Config.getFactory().parseTokens("OPERATION", null, t, "op");
		assertEquals("ident", testItem.getValue(), "expect parse item to have \"ident\" as the value");
	}

	@Test
	public final void testBadParseIdentBad() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "(~ ..\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase testItem = Config.getFactory().parseTokens("OPERATION", null, t, "op");
		assertEquals(ParserInternalTypeBase.EmptyType, testItem, "expect failed parse to return EmptyType");
	}
}
