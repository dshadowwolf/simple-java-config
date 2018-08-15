package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

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
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.EmptyParserType;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.ListParser;
import com.keildraco.config.testsupport.SupportClass.MockTokenizer;
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

		goodDataTokenizer = MockTokenizer.of(goodData);
		badDataTokenizer = MockTokenizer.of(badData);
		earlyEndDataTokenizer = MockTokenizer.of(earlyEndData);
		noDataTokenizer = MockTokenizer.noDataTokenizer();
		
		typeFactoryMock = mock(TypeFactory.class);
		keyValueParserMock = MockTokenizer.mockKeyValueParser();
		sectionParserMock = MockTokenizer.mockSectionParser();
		operationParserMock = MockTokenizer.mockOperationParser();
		
		doAnswer(invocation -> keyValueParserMock)
		.when(typeFactoryMock).getParser(eq("KEYVALUE"), any());
		doAnswer(invocation -> new ListParser(typeFactoryMock, null))
		.when(typeFactoryMock).getParser(eq("LIST"), any());
		doAnswer(invocation -> sectionParserMock)
		.when(typeFactoryMock).getParser(eq("SECTION"), any());
		doAnswer(invocation -> operationParserMock)
		.when(typeFactoryMock).getParser(eq("OPERATION"), any());
		
		doAnswer(invocation -> {
			ItemType type = invocation.getArgument(3);
			if(type == ItemType.LIST) {
				return new ListType(invocation.getArgument(1));
			} else if(type == ItemType.IDENTIFIER) {
				return new IdentifierType(invocation.getArgument(0),invocation.getArgument(1), invocation.getArgument(2));
			} else if(type == ItemType.OPERATION) {
				return new OperationType(invocation.getArgument(0), invocation.getArgument(1), invocation.getArgument(2));
			}
			return new EmptyParserType();
		})
		.when(typeFactoryMock).getType(any(), any(), any(), any(ItemType.class));
		
		// type factory "nextState()" mock
		doAnswer(invocation -> {
			String current = invocation.getArgument(0);
			Token currentToken = invocation.getArgument(1);
			Token nextToken = invocation.getArgument(2);

			switch(current) {
				case "SECTION":
					break;
				case "KEYVALUE":
					break;
				case "LIST":
					if(currentToken.getType() == TokenType.IDENTIFIER && nextToken.getType() == TokenType.OPEN_PARENS) return operationParserMock;
					else throw new UnknownStateException(String.format(
							"Transition state starting at %s with current as %s and next as %s is not known (%s :: %s)",
							current, currentToken.getType(), nextToken.getType(), currentToken.getValue(), nextToken.getValue()));
				default:
					throw new UnknownStateException(String.format(
							"Transition state starting at %s with current as %s and next as %s is not known (%s :: %s)",
							current, currentToken.getType(), nextToken.getType(), currentToken.getValue(), nextToken.getValue()));
			}
			return null;
		}).when(typeFactoryMock).nextState(any(String.class), any(Token.class), any(Token.class));

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
