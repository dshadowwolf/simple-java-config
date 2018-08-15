package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Deque;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.keildraco.config.Config;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.ListParser;
import com.keildraco.config.testsupport.MockSource;
import com.keildraco.config.testsupport.TypeFactoryMockBuilder;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class ListParserTest {

	private static final String	CAUGHT_EXCEPTION	= "Caught exception running loadFile: ";
	private static final String	EXCEPTION_GETTING	= "Exception getting type instance for {}: {}";

	private static Tokenizer goodDataTokenizer;
	private static Tokenizer noDataTokenizer;
	private static Tokenizer badDataTokenizer;
	private static Tokenizer earlyEndDataTokenizer;
	private static TypeFactory typeFactoryMock;
	private static IStateParser keyValueParserMock;
	private static IStateParser sectionParserMock;
	private static IStateParser operationParserMock;
	
	/**
	 *
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	@BeforeAll
	static void setupMocks() throws NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		Deque<Token> goodData = Lists.newLinkedList(Arrays.asList(new Token("["), new Token("alpha"), 
				new Token(","), new Token("beta"), new Token(","), new Token("charlie"), new Token("("),
				new Token("!"), new Token("delta"), new Token(")"), new Token("]")));
		Deque<Token> badData = Lists.newLinkedList(Arrays.asList(new Token("["), new Token("alpha"), 
				new Token("(")));
		Deque<Token> earlyEndData = Lists.newLinkedList(Arrays.asList(new Token("["), new Token("ash"), 
				new Token(","), new Token("blood"), new Token(","), new Token("choices")));

		goodDataTokenizer = MockSource.tokenizerOf(goodData);
		badDataTokenizer = MockSource.tokenizerOf(badData);
		earlyEndDataTokenizer = MockSource.tokenizerOf(earlyEndData);
		noDataTokenizer = MockSource.noDataTokenizer();
		
		typeFactoryMock = new TypeFactoryMockBuilder()
				.addType(ItemType.LIST, i -> new ListType(i.getArgument(1)))
				.addType(ItemType.IDENTIFIER, i -> new IdentifierType(i.getArgument(0),i.getArgument(1), i.getArgument(2)))
				.addType(ItemType.OPERATION, i -> new OperationType(i.getArgument(0), i.getArgument(1), i.getArgument(2)))
				.addState("KEYVALUE", i -> keyValueParserMock)
				.addState("OPERATION", i -> operationParserMock)
				.addState("SECTION", i -> sectionParserMock)
				.addState("LIST", i -> new ListParser(typeFactoryMock, null))
				.addTransition("LIST", TokenType.IDENTIFIER, TokenType.OPEN_PARENS, "OPERATION")
				.create();
		
		keyValueParserMock = MockSource.mockKeyValueParser();
		sectionParserMock = MockSource.mockSectionParser();
		operationParserMock = MockSource.mockOperationParser();		
	}

	/**
	 *
	 */
	@Test
	void testGetState() {
		IStateParser listParser = new ListParser(typeFactoryMock, null);
		final ParserInternalTypeBase pb = listParser.getState(goodDataTokenizer);
		assertAll("result is correct", () -> assertNotNull(pb, "result not null"),
				() -> assertTrue(pb.has("alpha"), "has member named alpha"),
				() -> assertFalse(pb.has("bravo"), "has no member named bravo"));
	}

	/**
	 *
	 */
	@Test
	void testListParser() {
		try {
			final ListParser op = new ListParser(typeFactoryMock, null);
			assertNotNull(op, "Able to instantiate a ListParser");
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
			final ListParser op = new ListParser(typeFactoryMock, null);
			op.registerTransitions(typeFactoryMock);
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
	void testErrorNoData() {
		IStateParser listParser = new ListParser(typeFactoryMock, null);
		assertThrows(IllegalParserStateException.class, () -> listParser.getState(noDataTokenizer));
	}

	/**
	 *
	 */
	@Test
	void testErrorBadData() {
		IStateParser listParser = new ListParser(typeFactoryMock, null);
		assertThrows(GenericParseException.class, () -> listParser.getState(badDataTokenizer));
	}

	/**
	 *
	 */
	@Test
	void testErrorEarlyEOF() {
		IStateParser listParser = new ListParser(typeFactoryMock, null);
		assertThrows(GenericParseException.class, () -> listParser.getState(earlyEndDataTokenizer));
	}
}
