package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Deque;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.keildraco.config.Config;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.KeyValueParser;
import com.keildraco.config.testsupport.MockSource;
import com.keildraco.config.testsupport.TypeFactoryMockBuilder;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class KeyValueParserTest {

	private static final String	CAUGHT_EXCEPTION	= "Caught exception running loadFile: ";
	private static final String	EXCEPTION_GETTING	= "Exception getting type instance for {}: {}";
	private static TypeFactory typeFactoryMock;
	private static Tokenizer goodDataokenizerMock;
	private static IStateParser listParserMock;
	private static IStateParser sectionParserMock;
	private static Tokenizer noDataTokenizerMock;

	@BeforeAll
	static void setupMocks() {
		Deque<Token> goodData = Lists.newLinkedList(Arrays.asList(new Token("key"), new Token("="), new Token("value")));
		
		typeFactoryMock = new TypeFactoryMockBuilder().addState("KEYVALUE", i -> new KeyValueParser(typeFactoryMock, null))
				.addState("LIST", i -> listParserMock)
				.addState("SECTION", i -> sectionParserMock)
				.addType(ItemType.IDENTIFIER, i -> new IdentifierType(i.getArgument(0), i.getArgument(1), i.getArgument(2)))
				.addTransition("KEYVALUE", TokenType.OPEN_LIST, TokenType.IDENTIFIER, "LIST")
				.create();
		
		goodDataokenizerMock = MockSource.tokenizerOf(goodData);
		listParserMock = MockSource.mockListParser();
		sectionParserMock = MockSource.mockSectionParser();
		noDataTokenizerMock = MockSource.noDataTokenizer();		
	}
	/**
	 *
	 */
	@Test
	void testGetState() {
		final IStateParser p = new KeyValueParser(typeFactoryMock, null);
		final ParserInternalTypeBase pb = p.getState(goodDataokenizerMock);
		assertAll("result is correct", () -> assertNotNull(pb, "result not null"),
				() -> assertEquals("key", pb.getName(), "name is correct"),
				() -> assertEquals("value", pb.getValueRaw(), "value is correct"));
	}

	/**
	 *
	 */
	@Test
	void testKeyValueParser() {
		try {
			final KeyValueParser kvp = new KeyValueParser(typeFactoryMock, null);
			assertNotNull(kvp, "Able to instantiate a KeyValueParser");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testRegisterTransitions() {
		try {
			final KeyValueParser kvp = new KeyValueParser(typeFactoryMock, null);
			kvp.registerTransitions(typeFactoryMock);
			assertTrue(true, "was able to register transitions");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testGetStateErrorRoutes() {
		final IStateParser p = new KeyValueParser(typeFactoryMock, null);
		assertThrows(IllegalParserStateException.class, () -> p.getState(noDataTokenizerMock),
						"No Data Equals Exception");
	}
}
