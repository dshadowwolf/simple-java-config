package com.keildraco.config.tests.states;

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
import com.keildraco.config.states.RootState;
import com.keildraco.config.testsupport.MockSource;
import com.keildraco.config.testsupport.TypeFactoryMockBuilder;
import com.keildraco.config.tokenizer.Tokenizer;

import static com.keildraco.config.Config.EMPTY_TYPE;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class RootStateTest {

	private static final String	CAUGHT_EXCEPTION	= "Caught exception running loadFile: ";
	private static final String	EXCEPTION_GETTING	= "Exception getting type instance for {}: {}";
	private static TypeFactory typeFactoryMock;
	private static Tokenizer noDataTokenizerMock;
	private static Tokenizer badDataTokenizerMock;
	private static IStateParser keyValueParserMock;
	private static IStateParser sectionParserMock;
	private static IStateParser operationParserMock;
	private static IStateParser listParserMock;

	@BeforeAll
	static void setup() {
		Deque<Token> badData = Lists.newLinkedList(Arrays.asList(new Token("op"), new Token("("), new Token("!"), new Token("ident"), new Token("ent"), new Token(")")));
		typeFactoryMock = new TypeFactoryMockBuilder()
				.addType(ItemType.LIST, (parent,name,value) -> MockSource.typeMockOf(ItemType.LIST, name, value))
				.addType(ItemType.IDENTIFIER, (parent,name,value) -> MockSource.typeMockOf(ItemType.IDENTIFIER, name, value))
				.addType(ItemType.OPERATION, (parent,name,value) -> MockSource.typeMockOf(ItemType.OPERATION, name, value))
				.addType(ItemType.SECTION, (parent,name,value) -> MockSource.typeMockOf(ItemType.SECTION, name, value))
				.addState("KEYVALUE", () -> keyValueParserMock)
				.addState("OPERATION", () -> operationParserMock)
				.addState("SECTION", () -> sectionParserMock)
				.addState("LIST", () -> listParserMock)
				.addTransition("ROOT", TokenType.IDENTIFIER, TokenType.OPEN_BRACE, "SECTION")
				.addTransition("ROOT", TokenType.IDENTIFIER, TokenType.STORE, "KEYVALUE")
				.create();
		
		keyValueParserMock = MockSource.mockKeyValueParser();
		sectionParserMock = MockSource.mockSectionParser();
		operationParserMock = MockSource.mockOperationParser();		
		listParserMock = MockSource.mockListParser();
		
		noDataTokenizerMock = MockSource.noDataTokenizer();
		badDataTokenizerMock = MockSource.tokenizerOf(badData);
	}

	/**
	 *
	 */
	@Test
	void testRootState() {
		try {
			final RootState rs = new RootState(typeFactoryMock, null);
			assertNotNull(rs, "Able to instantiate a RootState");
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
			final RootState rs = new RootState(typeFactoryMock, null);
			rs.registerTransitions(typeFactoryMock);
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
	void testErrorStateInParse() {
		RootState rs = new RootState(typeFactoryMock, null);
		assertEquals(EMPTY_TYPE, rs.getState(badDataTokenizerMock));
	}

	/**
	 *
	 */
	@Test
	void testErrorStateNoInput() {
		RootState rs = new RootState(typeFactoryMock, null);
		assertThrows(IllegalParserStateException.class, () -> rs.getState(noDataTokenizerMock));
	}
}
