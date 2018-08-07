package com.keildraco.config;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.keildraco.config.data.DataQuery;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.exceptions.TypeRegistrationException;
import com.keildraco.config.exceptions.ParserRegistrationException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.EmptyParserType;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;

import static com.keildraco.config.data.Constants.ParserNames.ROOT;

/**
 * <p>
 * Core API and access point for the entire configuration parsing and query system.
 * <p>
 * For information on loading configuration data from files, see:
 * <ul>
 * <li>{@link #loadFile(Path)}
 * <li>{@link #loadFile(String)}
 * <li>{@link #loadFile(URI)}
 * </ul>
 *
 * <p>
 * If you've already got an open InputStream you can use
 * {@link #parseStream(InputStream)}
 *
 * <p>
 * Loading information from a String is also relatively easy, see
 * {@link #parseString(String)}
 *
 * <p>
 * The fast method for using this system is:
 * <pre><code>
 * try {
 *   Config.reset();
 *   Config.registerKnownParts();
 *   DataQuery query = Config.loadFile("path/to/my_config_file.cfg");
 * } catch (IOException e) {
 *   List&lt;String&gt; bits = new LinkedList&lt;&gt;();
 *   bits.add(String.format("IOException trying to load and parse a configuration file: {}", e.getClass()));
 *   bits.add(String.format("Error Message: {}", e.getLocalizedMessage()));
 *   Arrays.asList(e.getStackTrace()).forEach(bits::add);
 *   Config.LOGGER.fatal(String.join(String.format("%n"), bits));
 * }
 * </code></pre>
 *
 * @author Daniel Hazelton
 *
 */
public final class Config {

	/**
	 * <p>
	 * Centralized logging facility. 'Nuff said.
	 */
	public static final Logger LOGGER = LogManager.getFormatterLogger("config");

	/**
	 * <p>
	 * Users shouldn't have to spin up their own {@link com.keildraco.config.factory.TypeFactory
	 * factory} for all the various {@link com.keildraco.config.types parse tree types} and
	 * {@link com.keildraco.config.states parser states}. This is to make sure they don't have to.
	 */
	private static final TypeFactory CORE_TYPE_FACTORY = new TypeFactory();

	/**
	 * <p>
	 * We strive to not return null anywhere or to use it as a parameter value. This helps.
	 */
	public static final ParserInternalTypeBase EMPTY_TYPE = new EmptyParserType();

	/**
	 * <p>
	 * Various types of {@link java.util.Map Maps}, specifically things like
	 * {@link java.util.HashMap HashMap&lt;&gt;()} and {@link java.util.concurrent.ConcurrentHashMap
	 * ConcurrentHashMap&lt;&gt;()} work best if they are given an initial size as part of their
	 * constructor. That is what this constant exists for.
	 */
	public static final int DEFAULT_HASH_SIZE = 256;

	/**
	 * Private default constructor. Do not call.
	 */
	private Config() {
		// do nothing, not even throw
	}

	/**
	 * Get the instance of the {@link com.keildraco.config.factory.TypeFactory TypeFactory}
	 * currently in use by this code.
	 *
	 * @return the current instance of the TypeFactory
	 */
	public static TypeFactory getFactory() {
		return CORE_TYPE_FACTORY;
	}

	/**
	 * Private helper. This is used as part of a lambda to generate parser instances.
	 *
	 * @param nameIn
	 *            Name of the parser
	 * @param clazz
	 *            Class the parser is based on.
	 *
	 * @throws SecurityException
	 *             Security Manager says No!
	 * @throws NoSuchMethodException
	 *             Thrown if the
	 *             {@link AbstractParserBase#AbstractParserBase(TypeFactory, ParserInternalTypeBase)
	 *             "TypeFactory, ParserInternalTypeBase"} constructor does not exist.
	 * @throws InvocationTargetException
	 *             The constructor itself threw an exception.
	 * @throws IllegalArgumentException
	 *             Arguments used are invalid.
	 * @throws IllegalAccessException
	 *             Thrown if the
	 *             {@link AbstractParserBase#AbstractParserBase(TypeFactory, ParserInternalTypeBase)
	 *             "TypeFactory, ParserInternalTypeBase"} constructor is not public.
	 * @throws InstantiationException
	 *             Something went wrong in the newInstance() call.
	 *
	 * @return An instance of an IStateParser based class.
	 */
	private static IStateParser registerParserGenerator(final Class<? extends IStateParser> clazz,
			final String nameIn) throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		final Constructor<? extends IStateParser> c = clazz.getConstructor(TypeFactory.class,
				ParserInternalTypeBase.class);
		final IStateParser cc = c.newInstance(CORE_TYPE_FACTORY, Config.EMPTY_TYPE);
		cc.registerTransitions(CORE_TYPE_FACTORY);
		cc.setName(nameIn);
		return cc;
	}

	/**
	 * Internal helper that creates and registers the lambda that uses
	 * {@link #registerParserGenerator} to produce instances of the class.
	 *
	 * @param name
	 *            Name to give the parser
	 * @param clazz
	 *            Class&lt; {@link com.keildraco.config.interfaces.IStateParser}&gt; to use to
	 *            generate new instances.
	 */
	private static void registerParserInternal(final String name,
			final Class<? extends IStateParser> clazz) {
		CORE_TYPE_FACTORY.registerParser(() -> {
			try {
				return registerParserGenerator(clazz, name);
			} catch (NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new ParserRegistrationException(name, e);
			}
		}, name);
	}

	/**
	 * Private helper. This is used as part of a lambda to generate parse tree type instances.
	 *
	 * @param parent
	 *            "Parent" of the instance. Intended for back-linking for some bookkeeping, this
	 *            feature is currently unused.
	 * @param name
	 *            Name of the element this is going to represent - this value is taken, nominally,
	 *            direct from the input data.
	 * @param value
	 *            Value for this element - this value is taken, nominally, direct from the input
	 *            data.
	 * @param clazz
	 *            Class the parse tree type is based on.
	 *
	 * @throws NoSuchMethodException
	 *             Thrown if the
	 *             {@link AbstractParserBase#AbstractParserBase(TypeFactory, ParserInternalTypeBase)
	 *             "TypeFactory, ParserInternalTypeBase"} constructor does not exist.
	 * @throws InvocationTargetException
	 *             The constructor itself threw an exception.
	 * @throws IllegalAccessException
	 *             Thrown if the
	 *             {@link AbstractParserBase#AbstractParserBase(TypeFactory, ParserInternalTypeBase)
	 *             "TypeFactory, ParserInternalTypeBase"} constructor is not public.
	 * @throws InstantiationException
	 *             Something went wrong in the newInstance() call.
	 *
	 * @return An instance of a ParserInternalTypeBase based class.
	 */
	private static ParserInternalTypeBase registerTypeGenerator(final ParserInternalTypeBase parent,
			final String name, final String value,
			final Class<? extends ParserInternalTypeBase> clazz) throws NoSuchMethodException,
			InstantiationException, IllegalAccessException, InvocationTargetException {
		final Constructor<? extends ParserInternalTypeBase> c = clazz
				.getConstructor(ParserInternalTypeBase.class, String.class, String.class);
		return c.newInstance(parent, name, value);
	}

	/**
	 * Internal helper - creates the lambda that calls
	 * {@link #registerTypeGenerator(ParserInternalTypeBase, String, String, Class)
	 * registerTypeGenerator} to get instances of the requested parse tree type.
	 *
	 * @param type
	 *            {@link com.keildraco.config.data.ItemType ItemType} value specifically designating
	 *            this parse tree items type.
	 * @param clazz
	 *            Class&lt;? extends {@link com.keildraco.config.interfaces.ParserInternalTypeBase
	 *            ParserInternalTypeBase}&gt; used to generate new instances.
	 */
	private static void registerTypeInternal(final ItemType type,
			final Class<? extends ParserInternalTypeBase> clazz) {
		CORE_TYPE_FACTORY.registerType((parent, name, value) -> {
			try {
				if (parent == null) {
					return registerTypeGenerator(EMPTY_TYPE, name, value, clazz);
				} else {
					return registerTypeGenerator(parent, name, value, clazz);
				}
			} catch (NoSuchMethodException | SecurityException | InstantiationException
					| IllegalAccessException | IllegalArgumentException
					| InvocationTargetException e) {
				throw new TypeRegistrationException(String.format(
						"Caught exception %s when trying to register type %s", e.getClass(), name));
			}
		}, type);
	}

	/**
	 * Wrapper around {@link #registerTypeInternal(ItemType, Class) registerTypeInternal}.
	 *
	 * @param type
	 *            {@link com.keildraco.config.data.ItemType ItemType} value specifically designating
	 *            this parse tree items type.
	 * @param clazz
	 *            Class&lt;? extends {@link com.keildraco.config.interfaces.ParserInternalTypeBase
	 *            ParserInternalTypeBase}&gt; used to generate new instances.
	 */
	public static void registerType(final ItemType type,
			final Class<? extends ParserInternalTypeBase> clazz) {
		registerTypeInternal(type, clazz);
	}

	/**
	 * Internal helper, exists to extract the {@link com.keildraco.config.data.ItemType ItemType}
	 * from the rest of the data and use that for the call to
	 * {@link #registerTypeInternal(ItemType, Class) registerTypeInternal}.
	 *
	 * @param clazz
	 *            Class&lt;? extends {@link com.keildraco.config.interfaces.ParserInternalTypeBase
	 *            ParserInternalTypeBase}&gt; used to generate new instances.
	 *
	 * @throws NoSuchMethodException
	 *             If the {@link ParserInternalTypeBase#ParserInternalTypeBase(String)} constructor
	 *             override is not present.
	 * @throws InstantiationException
	 *             Something happened during the newInstance() call needed to be able to call
	 *             {@link ParserInternalTypeBase#getType() getType()}.
	 * @throws IllegalAccessException
	 *             The {@link ParserInternalTypeBase#ParserInternalTypeBase(String)} constructor
	 *             override is not accessible.
	 * @throws InvocationTargetException
	 *             There was an exception thrown by the constructor.
	 */
	private static void registerType(final Class<? extends ParserInternalTypeBase> clazz)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		final Constructor<? extends ParserInternalTypeBase> cc = clazz.getConstructor(String.class);
		ParserInternalTypeBase zz = cc.newInstance("blargh");
		registerTypeInternal(zz.getType(), clazz);
	}

	/**
	 * Wrapper around {@link #registerParserInternal(String, Class) registerParserInternal}.
	 *
	 * @param name
	 *            The name of this parser.
	 * @param clazz
	 *            Class&lt; {@link com.keildraco.config.interfaces.IStateParser IStateParser}&gt;
	 *            used to generate new instances.
	 */
	public static void registerParser(final String name,
			final Class<? extends IStateParser> clazz) {
		registerParserInternal(name, clazz);
	}

	/**
	 * Internal helper, exists to extract the name of the parser from the rest of the data and use
	 * that for the call to {@link #registerParser(String, Class) registerParser}.
	 *
	 * @param clazz
	 *            Class&lt;? extends {@link com.keildraco.config.interfaces.IStateParser
	 *            IStateParser}&gt; used to generate new instances.
	 *
	 * @throws NoSuchMethodException
	 *             If the
	 *             {@link AbstractParserBase#AbstractParserBase(TypeFactory, ParserInternalBase)}
	 *             constructor override is not present.
	 * @throws InstantiationException
	 *             Something happened during the newInstance() call needed to be able to call
	 *             {@link IStateParser#getName() getName()}.
	 * @throws IllegalAccessException
	 *             The
	 *             {@link AbstractParserBase#AbstractParserBase(TypeFactory, ParserInternalBase)}
	 *             constructor override is not accessible.
	 * @throws InvocationTargetException
	 *             There was an exception thrown by the constructor.
	 */
	private static void registerParser(final Class<? extends IStateParser> clazz)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		final Constructor<? extends IStateParser> cc = clazz.getConstructor(TypeFactory.class,
				ParserInternalTypeBase.class);
		final IStateParser zz = cc.newInstance(CORE_TYPE_FACTORY, null);
		registerParserInternal(zz.getName(), clazz);
	}

	/**
	 * Register all classes found in {@link com.keildraco.config.types} and
	 * {@link com.keildraco.config.states} that extend either {@link ParserInternalTypeBase} or
	 * {@link IStateParser}.
	 */
	public static void registerKnownParts() {
		(new Reflections("com.keildraco.config.types")).getSubTypesOf(ParserInternalTypeBase.class)
				.stream().forEach(t -> {
					try {
						Config.registerType(t);
					} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
							| InvocationTargetException e) {
						throw new TypeRegistrationException(
								String.format("Caught exception %s when trying to register type %s",
										e.getClass(), t.getName()));
					}
				});
		(new Reflections("com.keildraco.config.states")).getSubTypesOf(AbstractParserBase.class)
				.stream().forEach(t -> {
					try {
						Config.registerParser(t);
					} catch (NoSuchMethodException | InstantiationException | IllegalAccessException
							| InvocationTargetException e) {
						throw new ParserRegistrationException(t.getName(), e);
					}
				});
	}

	/**
	 * Reset the TypeFactory instance to a clear and blank default state. This removes all type and
	 * parser registrations as well as parser state transition information.
	 */
	public static void reset() {
		CORE_TYPE_FACTORY.reset();
	}

	/**
	 * Internal helper to actually run a complete parse on a give {@link java.io.Reader Reader}.
	 *
	 * @param reader
	 *            The {@link java.io.Reader Reader} used to create the
	 *            {@link java.io.StreamTokenizer StreamTokenizer} used to setup the
	 *            {@link com.keildraco.config.tokenizer.Tokenizer Tokenizer}.
	 * @throws IOException
	 *             Something has gone wrong during the use of the Reader.
	 * @return An instance of {@link BaseState} downcast to {@link ParserInternalTypeBase}.
	 */
	private static ParserInternalTypeBase runParser(final Reader reader) throws IOException {
		final StreamTokenizer tok = new StreamTokenizer(reader);
		final Tokenizer t = new Tokenizer(tok);
		return CORE_TYPE_FACTORY.getParser(ROOT, null).getState(t);
	}

	/**
	 * Parse an open {@link java.io.InputStream InputStream} and return a {@link DataQuery} based on
	 * the result.
	 *
	 * @param inputStream The {@link InputStream} that is the source of the data to be parsed.
	 *
	 * @throws IOException
	 *             Something has gone wrong during the reading of the {@link java.io.InputStream
	 *             Input Stream}.
	 * @return A DataQuery object for checking the contents of the Parse Tree that represents the
	 *         configuration.
	 */
	public static DataQuery parseStream(final InputStream inputStream) throws IOException {
		final InputStreamReader baseReader = new InputStreamReader(inputStream,
				StandardCharsets.UTF_8);
		final ParserInternalTypeBase res = runParser(baseReader);
		return DataQuery.of(res);
	}

	/**
	 * Open a given {@link java.net.URI URI} and parse it.
	 *
	 * @param uri
	 *            The URI to open
	 *
	 * @throws IOException
	 *             Something has gone wrong during the reading of the {@link java.io.InputStream
	 *             Input Stream}.
	 * @return A DataQuery object for checking the contents of the Parse Tree that represents the
	 *         configuration.
	 */
	public static DataQuery loadFile(final URI uri) throws IOException {
		return parseStream(uri.toURL().openStream());
	}

	/**
	 * Open a given {@link java.nio.file.Path path} and parse it.
	 *
	 * @param filePath
	 *            The {@link java.nio.file.Path path} of the file.
	 *
	 * @throws IOException
	 *             Something has gone wrong during the reading of the {@link java.io.InputStream
	 *             Input Stream}.
	 * @throws URISyntaxException
	 *             Conversion of the Path into a {@link java.net.URI URI} has failed because the
	 *             path was not compatible with the requirements and restrictions of the URI syntax.
	 *
	 * @return A DataQuery object for checking the contents of the Parse Tree that represents the
	 *         configuration.
	 */
	public static DataQuery loadFile(final Path filePath) throws IOException, URISyntaxException {
		final String pathConversion = String.join("/", filePath.toString().split("\\\\"));
		return loadFile(pathConversion);
	}

	/**
	 * Given a files name and path to it as a string, find the file, open an InputStream for it and
	 * parse it.
	 *
	 * @param filePath
	 *            The full (relative or absolute) of the file to be opened.
	 *
	 * @throws IOException
	 *             Something has gone wrong during the reading of the {@link java.io.InputStream
	 *             Input Stream}.
	 * @throws URISyntaxException
	 *             Conversion of the Path into a {@link java.net.URI URI} has failed because the
	 *             path was not compatible with the requirements and restrictions of the URI syntax.
	 *
	 * @return A DataQuery object for checking the contents of the Parse Tree that represents the
	 *         configuration.
	 */
	public static DataQuery loadFile(final String filePath) throws IOException, URISyntaxException {
		final URL tu = Config.class.getClassLoader().getResource(filePath);
		if (tu != null) {
			final URI temp = tu.toURI();
			return loadFile(temp);
		} else {
			throw new IOException("URL was null!");
		}
	}

	/**
	 * Given a {@link java.lang.String String} containing configuration data of the correct format,
	 * convert it to an InputStream and parse it.
	 *
	 * @param data
	 *            The (hopefully correct and complete) config data to parse.
	 * @throws IOException
	 *             Something has gone wrong during the reading of the {@link java.io.InputStream
	 *             Input Stream}.
	 * @return A DataQuery object for checking the contents of the Parse Tree that represents the
	 *         configuration.
	 */
	public static DataQuery parseString(final String data) throws IOException {
		return parseStream(IOUtils.toInputStream(data, StandardCharsets.UTF_8));
	}
}
