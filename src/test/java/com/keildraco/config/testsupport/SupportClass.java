package com.keildraco.config.testsupport;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Deque;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.apache.commons.io.IOUtils;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.SectionType;

import static com.keildraco.config.Config.EMPTY_TYPE;
import com.keildraco.config.data.Constants;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

public final class SupportClass {

	private static final String	ABSTRACT				= "Abstract!";
	private static final String	TEST					= "TEST";
	private static final String	TESTING_PURPOSES_ONLY	= "Testing purposes only";
	private static final String	NULLPARSER				= "NULLPARSER";

	public static InputStream getInputStreamFromPath(final Path path)
			throws MalformedURLException, IOException, URISyntaxException {
		final String ts = String.join("/", path.toString().split("\\\\"));
		final URL tu = Config.class.getClassLoader().getResource(ts);
		final URI temp = tu.toURI();
		final InputStream is = temp.toURL().openStream();
		return is;
	}

	public static Tokenizer getTokenizerFromPath(final Path path)
			throws MalformedURLException, IOException, URISyntaxException {
		return new Tokenizer(new StreamTokenizer(
				new InputStreamReader(getInputStreamFromPath(path), StandardCharsets.UTF_8)));
	}

	public static InputStream getInputStreamFromString(final String data) {
		return IOUtils.toInputStream(data, StandardCharsets.UTF_8);
	}

	public static Tokenizer getTokenizerFromString(final String data)
			throws MalformedURLException, IOException, URISyntaxException {
		return new Tokenizer(new StreamTokenizer(
				new InputStreamReader(getInputStreamFromString(data), StandardCharsets.UTF_8)));
	}

	/**
	 *
	 * @param data
	 * @param parserName
	 * @throws IOException
	 * @throws IllegalParserStateException
	 * @throws UnknownStateException
	 * @throws GenericParseException
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 * @throws URISyntaxException
	 */
	public static ParserInternalTypeBase runParser(final String data, final String parserName)
			throws IOException, IllegalParserStateException, UnknownStateException,
			GenericParseException, NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException, URISyntaxException {
		Config.reset();
		Config.registerKnownParts();
		final IStateParser parser = Config.getFactory().getParser(parserName, null);
		final Tokenizer t = getTokenizerFromString(data);
		return parser.getState(t);
	}

	/**
	 *
	 * @author Daniel Hazelton
	 *
	 */
	public static final class ParserThatThrows extends AbstractParserBase {

		/**
		 *
		 * @param factory
		 * @param parent
		 * @throws IllegalAccessException
		 */
		ParserThatThrows(final TypeFactory factory, final ParserInternalTypeBase parent)
				throws IllegalArgumentException {
			super(factory, parent, TEST);
			throw new IllegalArgumentException(TESTING_PURPOSES_ONLY);
		}

		@Override
		public void registerTransitions(@Nullable final TypeFactory factory) {
			// not needed
		}

		@Override
		public ParserInternalTypeBase getState(@Nonnull final Tokenizer tokenizer) {
			return EMPTY_TYPE;
		}
	}

	/**
	 *
	 * @author Daniel Hazelton
	 *
	 *
	 */
	public static final class TypeThatThrows extends ParserInternalTypeBase {

		/**
		 *
		 * @param parentIn
		 * @param nameIn
		 * @param valueIn
		 * @throws IllegalAccessException
		 */
		TypeThatThrows(final ParserInternalTypeBase parentIn, final String nameIn,
				final String valueIn) throws GenericParseException {
			super(parentIn, nameIn, valueIn);
			throw new GenericParseException(TESTING_PURPOSES_ONLY);
		}

		@Override
		public String getValue() {
			return ABSTRACT;
		}

		@Override
		public String getValueRaw() {
			return this.getValue();
		}
	}

	/**
	 *
	 * @param name
	 * @return
	 */
	public static ParserInternalTypeBase getInstance(final String name) {
		return new ParserInternalTypeBase(name) {

			@Override
			@Nonnull
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return ABSTRACT;
			}
		};
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @return
	 */
	public static ParserInternalTypeBase getInstance(final ParserInternalTypeBase parent,
			final String name) {
		return new ParserInternalTypeBase(parent, name) {

			@Nonnull
			@Override
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return ABSTRACT;
			}
		};
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param value
	 * @return
	 */
	public static ParserInternalTypeBase getInstance(final ParserInternalTypeBase parent,
			final String name, final String value) {
		return new ParserInternalTypeBase(parent, name, value) {

			@Nonnull
			@Override
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return ABSTRACT;
			}
		};
	}

	/**
	 *
	 * @author Daniel Hazelton
	 *
	 */
	public static final class NullParser extends AbstractParserBase {

		private static boolean flag = false;

		/**
		 *
		 * @param factory
		 * @param parent
		 */
		public NullParser(final TypeFactory factory, final ParserInternalTypeBase parent) {
			super(factory, parent, NULLPARSER);
		}

		@Override
		public void registerTransitions(@Nullable final TypeFactory factory) {
			// blank
		}

		@Override
		public ParserInternalTypeBase getState(@Nonnull final Tokenizer tokenizer) {
			return EMPTY_TYPE;
		}

		public static boolean getFlag() {
			return flag;
		}

		public static void setFlag() {
			flag = true;
		}
	}
	
	public static class MockTokenizer {
		public static Tokenizer of(Deque<Token> tokens) {
			Tokenizer resp = mock(Tokenizer.class);
			
			doAnswer( invocation -> {
				if (tokens.isEmpty()) {
					return new Token(Constants.TOKENEMPTY);
				}

				return tokens.peek();
			}).when(resp).peek();

			doAnswer( invocation ->  {
				if ((tokens.isEmpty()) || (tokens.size() == 1)) {
					return new Token(Constants.TOKENEMPTY);
				}

				final Token tok = tokens.pop();
				final Token rv = tokens.peek();

				tokens.push(tok);
				return rv;
			}).when(resp).peekToken();

			doAnswer(invocation -> {
				Token val = invocation.getArgument(0);
				tokens.push(val);
				return null;
			}).when(resp).pushBack(any(Token.class));

			doAnswer(invocation -> !tokens.isEmpty()).when(resp).hasNext();

			doAnswer(invocation -> {
				if (tokens.isEmpty()) {
					return new Token(Constants.TOKENEMPTY);
				}

				Token rv = tokens.pop();

				return rv;
			}).when(resp).nextToken();

			return resp;
		}
		
		public static Tokenizer noDataTokenizer() {
			Tokenizer resp = mock(Tokenizer.class);
			doAnswer(i -> new Token(Constants.TOKENEMPTY)).when(resp).nextToken();
			doAnswer(i -> new Token(Constants.TOKENEMPTY)).when(resp).peek();
			doAnswer(i -> new Token(Constants.TOKENEMPTY)).when(resp).peekToken();
			doAnswer(i -> false).when(resp).hasNext();
			return resp;
		}
		
		public static IStateParser mockListParser() {
			IStateParser resp = mock(IStateParser.class);
			
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
			.when(resp).getState(any(Tokenizer.class));
			
			return resp;
		}
		
		public static IStateParser mockKeyValueParser() {
			IStateParser resp = mock(IStateParser.class);
			
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
					return mockListParser().getState(t);
				}
				
				t.nextToken(); // consume
				return new IdentifierType("key", "value");
			})
			.when(resp).getState(any(Tokenizer.class));
			
			return resp;
		}
		
		public static IStateParser mockSectionParser() {
			IStateParser resp = mock(IStateParser.class);
			
			doAnswer( invocation -> {
				Tokenizer t = invocation.getArgument(0);
				Token tt = t.nextToken();
				while(t.hasNext() && tt.getType() != TokenType.CLOSE_BRACE) tt = t.nextToken();
				if(!t.hasNext() && tt.getType() != TokenType.CLOSE_BRACE)
					throw new GenericParseException("Early End of Data");
				else
					tt = t.nextToken();
				
				return new SectionType("section");
			})
			.when(resp).getState(any(Tokenizer.class));
			
			return resp;
		}

	}
}
