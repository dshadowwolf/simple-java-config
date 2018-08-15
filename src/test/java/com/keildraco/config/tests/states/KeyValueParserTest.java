package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

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
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.KeyValueParser;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;

import static com.keildraco.config.testsupport.SupportClass.MockTokenizer;

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
		
		typeFactoryMock = mock(TypeFactory.class);
		goodDataokenizerMock = MockTokenizer.of(goodData);
		listParserMock = MockTokenizer.mockListParser();
		sectionParserMock = MockTokenizer.mockSectionParser();
		noDataTokenizerMock = MockTokenizer.noDataTokenizer();
		
		// setup type factory
		doAnswer(invocation -> new KeyValueParser(typeFactoryMock, null))
		.when(typeFactoryMock).getParser(eq("KEYVALUE"), any());
		doAnswer(invocation -> listParserMock)
		.when(typeFactoryMock).getParser(eq("LIST"), any());
		doAnswer(invocation -> sectionParserMock)
		.when(typeFactoryMock).getParser(eq("SECTION"), any());
		doAnswer(invocation -> {
			ItemType type = invocation.getArgument(3);
			if(type == ItemType.IDENTIFIER) {
				return new IdentifierType(null, invocation.getArgument(1), invocation.getArgument(2));
			}
			return null;
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
					if(currentToken.getType() == TokenType.OPEN_LIST && nextToken.getType() == TokenType.IDENTIFIER) return listParserMock;
					else throw new UnknownStateException(String.format(
							"Transition state starting at %s with current as %s and next as %s is not known (%s :: %s)",
							current, currentToken.getType(), nextToken.getType(), currentToken.getValue(), nextToken.getValue()));
				case "LIST":
					break;
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
