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
import com.keildraco.config.data.ItemMatcher;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.IStateParser;
import com.keildraco.config.states.KeyValueParser;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;
import com.keildraco.config.types.SectionType;

@TestInstance(Lifecycle.PER_CLASS)
public class KeyValueParserTest {

	private TypeFactory factory;

	/**
	 *
	 * @throws Exception
	 */
	@BeforeAll
	public void setUp() throws Exception {

		this.factory = new TypeFactory();
		this.factory.registerParser(() -> {
			final IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class)))
					.thenAnswer(new Answer<ParserInternalTypeBase>() {

						public ParserInternalTypeBase answer(final InvocationOnMock invocation)
								throws Throwable {
							while (((StreamTokenizer) invocation.getArgument(0))
									.nextToken() != StreamTokenizer.TT_EOF
									&& ((StreamTokenizer) invocation.getArgument(0)).ttype != ']')
								;

							if (((StreamTokenizer) invocation.getArgument(0)).ttype == ']') {
								((StreamTokenizer) invocation.getArgument(0)).nextToken();
							}
							return new ListType(null, "", "");
						}
					});

			when(p.getName()).thenAnswer(new Answer<String>() {

				public String answer(final InvocationOnMock invocation) throws Throwable {
					return "MockType";
				}
			});
			return p;
		}, "LIST");
		this.factory.registerParser(() -> {
			final IStateParser p = mock(IStateParser.class);
			when(p.getState(isA(StreamTokenizer.class)))
					.thenAnswer(new Answer<ParserInternalTypeBase>() {

						public ParserInternalTypeBase answer(final InvocationOnMock invocation)
								throws Throwable {
							final StreamTokenizer tok = (StreamTokenizer) invocation.getArgument(0);
							while (tok.nextToken() != StreamTokenizer.TT_EOF && tok.ttype != ')')
								;

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
		this.factory.registerParser(() -> new KeyValueParser(this.factory, "KEYVALUE"), "KEYVALUE");
		this.factory.registerType((parent, name, value) -> new IdentifierType(parent, name, value),
				ItemType.IDENTIFIER);
		this.factory.registerType((parent, name, value) -> new ListType(parent, name, value),
				ItemType.LIST);
		this.factory.registerType((parent, name, value) -> new OperationType(parent, name, value),
				ItemType.OPERATION);
		this.factory.registerType((parent, name, value) -> new SectionType(parent, name, value),
				ItemType.SECTION);
	}

	@Test
	public final void testKeyValueParser() {
		try {
			@SuppressWarnings("unused")
			final KeyValueParser p = new KeyValueParser(this.factory, "KEYVALUE");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instanting a new KeyValueParser: " + e.getMessage());
		}
	}

	@Test
	public final void testGetState() {
		final String testString = "[ a_value, another_value, a_third_value ]\n\n";
		final InputStreamReader isr = new InputStreamReader(
				IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = this.factory.parseTokens("KEYVALUE", null, t, "a_key");
		assertEquals("a_key = [  ]", k.asString());
	}

	@Test
	public final void testEarlyEOF() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "an_ident = \n\n";
		final InputStreamReader isr = new InputStreamReader(
				IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = Config.getFactory().parseTokens("SECTION", null, t,
				"blargh");
		assertEquals(ParserInternalTypeBase.EmptyType, k, "expect EmptyType due to malformation");
	}

	@Test
	public final void testEarlySectionEnd() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "an_ident = }\n\n";
		final InputStreamReader isr = new InputStreamReader(
				IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = Config.getFactory().parseTokens("SECTION", null, t,
				"blargh");
		assertEquals(ParserInternalTypeBase.EmptyType, k, "expect EmptyType due to malformation");
	}

	@Test
	public final void testNonWordWhereIdentExpected() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "an_ident = ;\n\n";
		final InputStreamReader isr = new InputStreamReader(
				IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = Config.getFactory().parseTokens("SECTION", null, t,
				"blargh");
		assertEquals(ParserInternalTypeBase.EmptyType, k, "expect EmptyType due to malformation");
	}

	@Test
	public final void testBadFormat() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "an_ident = other\n";
		final InputStreamReader isr = new InputStreamReader(
				IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final IStateParser p = Config.getFactory().getParser("KEYVALUE", null);
		p.setErrored();
		final ParserInternalTypeBase k = p.getState(t);
		assertEquals(ParserInternalTypeBase.EmptyType, k, "expect EmptyType due to malformation");
	}

	@Test
	public final void testFindsOperation() {
		Config.reset();
		Config.registerKnownParts();
		final String testString = "other(!misc)\n";
		final InputStreamReader isr = new InputStreamReader(
				IOUtils.toInputStream(testString, StandardCharsets.UTF_8), StandardCharsets.UTF_8);
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		final ParserInternalTypeBase k = Config.getFactory().parseTokens("KEYVALUE", null, t,
				"other");
		assertTrue((new ItemMatcher(k)).matches("other.etc"), "expect value match");
	}
}
