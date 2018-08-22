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
	private static Tokenizer withOperationDataokenizerMock;
	private static IStateParser listParserMock;
	private static IStateParser sectionParserMock;
	private static IStateParser operationParserMock;
	private static Tokenizer noDataTokenizerMock;

	@BeforeAll
	static void setupMocks() {
		Deque<Token> goodData = Lists.newLinkedList(Arrays.asList(new Token("key"), new Token("="), new Token("value")));
		Deque<Token> withOperationData = Lists.newLinkedList(Arrays.asList(new Token("key"), new Token("="), 
				new Token("oper"), new Token("("), new Token("!"), new Token("value"), new Token(")")));

		typeFactoryMock = new TypeFactoryMockBuilder().addState("KEYVALUE", () -> new KeyValueParser(typeFactoryMock, null))
				.addState("LIST", () -> listParserMock)
				.addState("SECTION", () -> sectionParserMock)
				.addState("OPERATION", () -> operationParserMock)
				.addType(ItemType.IDENTIFIER, (parent, name, value) -> MockSource.typeMockOf(ItemType.IDENTIFIER, name, value))
				.addType(ItemType.OPERATION, (parent, name, value) -> MockSource.typeMockOf(ItemType.OPERATION, name, value))
				.addTransition("KEYVALUE", TokenType.OPEN_LIST, TokenType.IDENTIFIER, "LIST")
				.addTransition("KEYVALUE", TokenType.IDENTIFIER, TokenType.OPEN_PARENS, "OPERATION")
				.create();
		
		goodDataokenizerMock = MockSource.tokenizerOf(goodData);
		withOperationDataokenizerMock = MockSource.tokenizerOf(withOperationData);
		listParserMock = MockSource.mockListParser();
		sectionParserMock = MockSource.mockSectionParser();
		operationParserMock = MockSource.mockOperationParser();
		noDataTokenizerMock = MockSource.noDataTokenizer();		
	}
	/**
	 *
	 */
	@Test
	void testGetState() {
		final IStateParser p = new KeyValueParser(typeFactoryMock, null);
		final ParserInternalTypeBase pb = p.getState(goodDataokenizerMock);
		final ParserInternalTypeBase opb = p.getState(withOperationDataokenizerMock);
		assertAll("result is correct", () -> assertNotNull(pb, "result not null"),
				() -> assertEquals("key", pb.getName(), "name is correct"),
				() -> assertEquals("value", pb.getValueRaw(), "value is correct"),
				() -> assertEquals("oper(! value)", opb.getValue(), "operation as value parsed correctly"),
				() -> assertEquals("oper", opb.getName(), "operation as value, name is correct"));
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
