package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Deque;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.states.SectionParser;
import com.keildraco.config.testsupport.MockSource;
import com.keildraco.config.testsupport.TypeFactoryMockBuilder;
import com.keildraco.config.tokenizer.Tokenizer;

import static com.keildraco.config.Config.EMPTY_TYPE;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class SectionParserTest {

	private static final String	CAUGHT_EXCEPTION	= "Caught exception running loadFile: ";
	private static final String	EXCEPTION_GETTING	= "Exception getting type instance for {}: {}";
	private static IStateParser keyValueParserMock;
	private static IStateParser listParserMock;
	private static TypeFactory typeFactoryMock;
	private static Tokenizer goodDataTokenizerMock;
	private static Tokenizer earlyEndDataTokenizerMock;
	private static Tokenizer badDataTokenizerMock;
	private static Tokenizer noDataTokenizerMock;
	
	@BeforeAll
	static void setupMocks() {
		keyValueParserMock = MockSource.mockKeyValueParser();
		listParserMock = MockSource.mockListParser();
		typeFactoryMock = new TypeFactoryMockBuilder()
				.addType(ItemType.SECTION, (parent,name,value) -> MockSource.typeMockOf(ItemType.SECTION, name, value))
				.addState("KEYVALUE", () -> keyValueParserMock)
		        .addState("LIST", () -> listParserMock)
		        .addState("SECTION", () -> new SectionParser(typeFactoryMock, null))
		        .addTransition("SECTION", TokenType.IDENTIFIER, TokenType.STORE, "KEYVALUE")
		        .addTransition("SECTION", TokenType.IDENTIFIER, TokenType.OPEN_BRACE, "SECTION")
		        .create();
		
		// data for tokenizer mocks
		Deque<Token> goodData = Lists.newLinkedList(Arrays.asList(new Token("section"), new Token("{"), new Token("key"), new Token("="), new Token("value"), new Token("}")));
		Deque<Token> earlyEndData = Lists.newLinkedList(Arrays.asList(new Token("section"), new Token("{"), new Token("key"), new Token("="), new Token("value")));
		Deque<Token> badData = Lists.newLinkedList(Arrays.asList(new Token("section"), new Token("["), new Token("blargh"), new Token("]")));
		
		goodDataTokenizerMock = MockSource.tokenizerOf(goodData);
		earlyEndDataTokenizerMock = MockSource.tokenizerOf(earlyEndData);
		badDataTokenizerMock = MockSource.tokenizerOf(badData);
		noDataTokenizerMock = MockSource.noDataTokenizer();
	}

	ParserInternalTypeBase runParser(final Tokenizer whichTokenizer) {
		SectionParser p = new SectionParser(typeFactoryMock, null);
		return p.getState(whichTokenizer);
	}
	
	/**
	 *
	 */
	@Test
	void testGetStateGood() {
		assertNotSame(EMPTY_TYPE, runParser(goodDataTokenizerMock),
				"standard parse works");
	}
		
	/**
	 *
	 */
	@Test
	void testGetStateGenericParseException() {
		assertThrows(GenericParseException.class,
				() -> runParser(earlyEndDataTokenizerMock));
	}
	
	/**
	 *
	 */
	@Test
	void testGetStateIllegalParserStateException() {
		assertThrows(IllegalParserStateException.class,
				() -> runParser(noDataTokenizerMock));
	}
	
	/**
	 *
	 */
	@Test
	void testGetStateUnknownStateException() {
		assertThrows(UnknownStateException.class, () -> runParser(badDataTokenizerMock));
	}
	
	/**
	 *
	 */
	@Test
	void testSectionParser() {
		try {
			final SectionParser sp = new SectionParser(typeFactoryMock, null);
			assertNotNull(sp, "Able to instantiate a SectionParser");
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
			final TypeFactory tf = new TypeFactory();
			final SectionParser sp = new SectionParser(tf, null);
			sp.registerTransitions(tf);
			assertTrue(true, "was able to register transitions");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}
}
