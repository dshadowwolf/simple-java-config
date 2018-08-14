package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;

import com.google.common.collect.Lists;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.Constants;
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
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.SectionType;

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
		keyValueParserMock = mock(IStateParser.class);
		listParserMock = mock(IStateParser.class);
		typeFactoryMock = mock(TypeFactory.class);
		goodDataTokenizerMock = mock(Tokenizer.class);
		earlyEndDataTokenizerMock = mock(Tokenizer.class);
		badDataTokenizerMock = mock(Tokenizer.class);
		noDataTokenizerMock = mock(Tokenizer.class);

		// tokenizer mock setup start
		
		// setup mock for list parser
		doAnswer( invocation -> {
			Tokenizer t = invocation.getArgument(0);
			Token tt = t.nextToken();
			while(t.hasNext() && tt.getType() != TokenType.CLOSE_LIST) tt = t.nextToken();
			if(!t.hasNext() && tt.getType() != TokenType.CLOSE_LIST)
				throw new GenericParseException("Early End of Data");
			else
				tt = t.nextToken();
			
			return new ListType("blargh", Collections.emptyList());
		})
		.when(listParserMock).getState(any(Tokenizer.class));

		// setup key-value parser
		doAnswer( invocation -> {
			Tokenizer t = invocation.getArgument(0);
			if(t.peek().getType() == TokenType.IDENTIFIER && t.peekToken().getType() == TokenType.STORE) {
				t.nextToken(); // consume opening ident
				t.nextToken(); // consume store
			}
			
			if(t.peek().getType() == TokenType.IDENTIFIER && t.peekToken().getType() == TokenType.OPEN_PARENS) {
				TokenType ntt = t.peek().getType();
				
				while(ntt != TokenType.CLOSE_PARENS && ntt != TokenType.EMPTY)
					ntt = t.nextToken().getType();
				
				if(t.peek().getType() == TokenType.EMPTY)
					throw new GenericParseException("Early End of Data");
				else if(t.peek().getType() == TokenType.CLOSE_PARENS)
					t.nextToken();
			} else if ( t.peek().getType() == TokenType.OPEN_LIST ){
				return listParserMock.getState(t);
			}
			
			t.nextToken(); // consume
			return new IdentifierType("key", "value");
		})
		.when(keyValueParserMock).getState(any(Tokenizer.class));

		// setup type factory
		when(typeFactoryMock.getParser(eq("KEYVALUE"), any())).thenReturn(keyValueParserMock);
		when(typeFactoryMock.getParser(eq("LIST"), any())).thenReturn(listParserMock);
		when(typeFactoryMock.getParser(eq("SECTION"), any())).thenReturn(new SectionParser(typeFactoryMock, null));
		when(typeFactoryMock.getType(any(), any(), any(), eq(ItemType.SECTION))).thenReturn(new SectionType("section"));
		
		// data for tokenizer mocks
		Deque<Token> goodData = Lists.newLinkedList(Arrays.asList(new Token("section"), new Token("{"), new Token("key"), new Token("="), new Token("value"), new Token("}")));
		Deque<Token> earlyEndData = Lists.newLinkedList(Arrays.asList(new Token("section"), new Token("{"), new Token("key"), new Token("="), new Token("value")));
		Deque<Token> badData = Lists.newLinkedList(Arrays.asList(new Token("section"), new Token("[")));

		// type factory "nextState()" mock
		doAnswer(invocation -> {
			String current = invocation.getArgument(0);
			Token currentToken = invocation.getArgument(1);
			Token nextToken = invocation.getArgument(2);
			
			switch(current) {
				case "SECTION":
					if(currentToken.getType() == TokenType.IDENTIFIER && nextToken.getType() == TokenType.STORE) return keyValueParserMock;
					else if(currentToken.getType() == TokenType.IDENTIFIER && nextToken.getType() == TokenType.OPEN_BRACE) return new SectionParser(typeFactoryMock, null);
					else throw new UnknownStateException(String.format(
							"Transition state starting at %s with current as %s and next as %s is not known (%s :: %s)",
							current, currentToken.getType(), nextToken.getType(), currentToken.getValue(), nextToken.getValue()));

				case "KEYVALUE":
					break;
				case "LIST":
					break;
				default:
					throw new UnknownStateException(String.format(
							"Transition state starting at %s with current as %s and next as %s is not known (%s :: %s)",
							current, currentToken.getType(), nextToken.getType(), currentToken.getValue(), nextToken.getValue()));
			}
			return null;
		}).when(typeFactoryMock).nextState(any(String.class), any(Token.class), any(Token.class));
		
		// Tokenizer with "good data" mock starts here
		doAnswer( invocation -> {
			if (goodData.isEmpty()) {
				return new Token(Constants.TOKENEMPTY);
			}
			
			return goodData.peek();
		}).when(goodDataTokenizerMock).peek();
		
		doAnswer( invocation ->  {
				if ((goodData.isEmpty()) || (goodData.size() == 1)) {
					return new Token(Constants.TOKENEMPTY);
				}

				final Token tok = goodData.pop();
				final Token rv = goodData.peek();

				goodData.push(tok);
				return rv;
			}).when(goodDataTokenizerMock).peekToken();
		
		doAnswer(invocation -> {
			Token val = invocation.getArgument(0);
				goodData.push(val);
				return null;
			}).when(goodDataTokenizerMock).pushBack(any(Token.class));
		
		doAnswer(invocation -> !goodData.isEmpty()).when(goodDataTokenizerMock).hasNext();

		doAnswer(invocation -> {
			if (goodData.isEmpty()) {
				return new Token(Constants.TOKENEMPTY);
			}
			
			Token rv = goodData.pop();
			
			return rv;
		}).when(goodDataTokenizerMock).nextToken();

		// Tokenizer with "bad data" mock starts here
		doAnswer( invocation -> {
			if (badData.isEmpty()) {
				return new Token(Constants.TOKENEMPTY);
			}
			
			return badData.peek();
		}).when(badDataTokenizerMock).peek();
	
		doAnswer( invocation -> {
			if ((badData.isEmpty()) || (badData.size() == 1)) {
				return new Token(Constants.TOKENEMPTY);
			}

			final Token tok = badData.pop();
			final Token rv = badData.peek();
			badData.push(tok);
			return rv;
		}).when(badDataTokenizerMock).peekToken();
		
		doAnswer(invocation -> !badData.isEmpty()).when(badDataTokenizerMock).hasNext();

		doAnswer(invocation -> {
			if (badData.isEmpty()) {
				return new Token(Constants.TOKENEMPTY);
			}
			
			return badData.pop();
		}).when(badDataTokenizerMock).nextToken();

		// Tokenizer with data that ends early starts here
		doAnswer( invocation -> {
			if (earlyEndData.isEmpty()) {
				return new Token(Constants.TOKENEMPTY);
			}
			
			return earlyEndData.peek();
		}).when(earlyEndDataTokenizerMock).peek();
		
		doAnswer(invocation -> {
				if ((earlyEndData.isEmpty()) || (earlyEndData.size() == 1)) {
					return new Token(Constants.TOKENEMPTY);
				}

				final Token tok = earlyEndData.pop();
				final Token rv = earlyEndData.peek();
				earlyEndData.push(tok);
				return rv;
			}).when(earlyEndDataTokenizerMock).peekToken();
		
		doAnswer(invocation -> {
			Token val = invocation.getArgument(0);
				earlyEndData.push(val);
				return null;
			}).when(earlyEndDataTokenizerMock).pushBack(any(Token.class));
		
		doAnswer(invocation -> !earlyEndData.isEmpty()).when(earlyEndDataTokenizerMock).hasNext();

		doAnswer(invocation -> {
			if (earlyEndData.isEmpty()) {
				return new Token(Constants.TOKENEMPTY);
			}
			
			return earlyEndData.pop();
		}).when(earlyEndDataTokenizerMock).nextToken();

		when(noDataTokenizerMock.nextToken()).thenReturn(new Token(Constants.TOKENEMPTY));
		when(noDataTokenizerMock.peekToken()).thenReturn(new Token(Constants.TOKENEMPTY));
		when(noDataTokenizerMock.peek()).thenReturn(new Token(Constants.TOKENEMPTY));
		when(noDataTokenizerMock.hasNext()).thenReturn(false);
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
