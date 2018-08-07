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
import static com.keildraco.config.Config.EMPTY_TYPE;


public final class SupportClass {

	private static final String ABSTRACT = "Abstract!";
	private static final String TEST = "TEST";
	private static final String TESTING_PURPOSES_ONLY = "Testing purposes only";
	private static final String NULLPARSER = "NULLPARSER";

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
		public ParserInternalTypeBase getState(Tokenizer tokenizer) {
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
		public ParserInternalTypeBase getState(final Tokenizer tokenizer) {
			return EMPTY_TYPE;
		}
		
		public static boolean getFlag() {
			return flag;
		}
		
		public static void setFlag() {
			flag = true;
		}
	}
}
