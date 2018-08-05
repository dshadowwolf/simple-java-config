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
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.reflections.Reflections;

import com.keildraco.config.data.DataQuery;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.AbstractParserBase;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;
import com.keildraco.config.tokenizer.Tokenizer;

import javax.annotation.Nullable;

/**
 *
 * @author Daniel Hazelton
 *
 */
public final class Config {

	/**
	 *
	 */
	public static final Logger LOGGER = LogManager.getFormatterLogger("config");

	/**
	 *
	 */
	private static TypeFactory coreTypeFactory = new TypeFactory();

	/**
	 *
	 */
	private Config() {
		// do nothing, not even throw
	}

	/**
	 *
	 * @return
	 */
	public static TypeFactory getFactory() {
		return coreTypeFactory;
	}

	/**
	 *
	 * @param name
	 * @param clazz
	 * @return
	 */
	@Nullable
	private static IStateParser registerParserGenerator(final String name,
			final Class<? extends IStateParser> clazz) {
		try {
			final Constructor<? extends IStateParser> c = clazz.getConstructor(TypeFactory.class,
					ParserInternalTypeBase.class);
			final IStateParser cc = c.newInstance(coreTypeFactory,
					ParserInternalTypeBase.EMPTY_TYPE);
			cc.registerTransitions(coreTypeFactory);
			return cc;
		} catch (final NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Exception getting type instance for %s (%s): %s", name, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(LOGGER::error);
			return null;
		}
	}

	/**
	 *
	 * @param name
	 * @param clazz
	 */
	private static void registerParserInternal(final String name,
			final Class<? extends IStateParser> clazz) {
		coreTypeFactory.registerParser(() -> registerParserGenerator(name, clazz), name);
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param value
	 * @param clazz
	 * @return
	 */
	@Nullable
	private static ParserInternalTypeBase registerTypeGenerator(final ParserInternalTypeBase parent,
			final String name, final String value,
			final Class<? extends ParserInternalTypeBase> clazz) {
		try {
			final Constructor<? extends ParserInternalTypeBase> c = clazz
					.getConstructor(ParserInternalTypeBase.class, String.class, String.class);
			return c.newInstance(parent, name, value);
		} catch (final NoSuchMethodException | SecurityException | InstantiationException
				| IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			LOGGER.error("Exception getting type instance for %s (%s): %s", name, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(LOGGER::error);
			return null;
		}
	}

	/**
	 *
	 * @param type
	 * @param clazz
	 */
	private static void registerTypeInternal(final ItemType type,
			final Class<? extends ParserInternalTypeBase> clazz) {
		coreTypeFactory.registerType(
				(parent, name, value) -> registerTypeGenerator(parent, name, value, clazz), type);
	}

	/**
	 *
	 * @param type
	 * @param clazz
	 */
	public static void registerType(final ItemType type,
			final Class<? extends ParserInternalTypeBase> clazz) {
		registerTypeInternal(type, clazz);
	}

	/**
	 *
	 * @param clazz
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static void registerType(final Class<? extends ParserInternalTypeBase> clazz)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		final Constructor<? extends ParserInternalTypeBase> cc = clazz.getConstructor(String.class);
		ParserInternalTypeBase zz = cc.newInstance("blargh");
		registerType(zz.getType(), clazz);
	}

	/**
	 *
	 * @param name
	 * @param clazz
	 */
	public static void registerParser(final String name,
			final Class<? extends IStateParser> clazz) {
		registerParserInternal(name, clazz);
	}

	/**
	 *
	 * @param clazz
	 * @throws NoSuchMethodException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 * @throws InvocationTargetException
	 */
	private static void registerParser(final Class<? extends IStateParser> clazz)
			throws NoSuchMethodException, InstantiationException, IllegalAccessException,
			InvocationTargetException {
		final Constructor<? extends IStateParser> cc = clazz.getConstructor(TypeFactory.class,
				ParserInternalTypeBase.class);
		final IStateParser zz = cc.newInstance(coreTypeFactory, null);
		registerParser(zz.getName(), clazz);
	}

	/**
	 * @throws InvocationTargetException
	 * @throws IllegalArgumentException
	 * @throws IllegalAccessException
	 * @throws InstantiationException
	 * @throws SecurityException
	 * @throws NoSuchMethodException
	 *
	 */
	public static void registerKnownParts() throws NoSuchMethodException, InstantiationException,
			IllegalAccessException, InvocationTargetException {
		final Reflections typeRefs = new Reflections("com.keildraco.config.types");
		final Reflections parserRefs = new Reflections("com.keildraco.config.states");
		final List<Class<? extends ParserInternalTypeBase>> types = typeRefs
				.getSubTypesOf(ParserInternalTypeBase.class).stream().collect(Collectors.toList());

		for (final Class<? extends ParserInternalTypeBase> type : types) {
			registerType(type);
		}

		final List<Class<? extends IStateParser>> parsers = parserRefs
				.getSubTypesOf(AbstractParserBase.class).stream().collect(Collectors.toList());

		for (final Class<? extends IStateParser> parser : parsers) {
			registerParser(parser);
		}
	}

	/**
	 *
	 */
	public static void reset() {
		coreTypeFactory.reset();
	}

	/**
	 *
	 * @param reader
	 * @return
	 * @throws IOException
	 */
	private static ParserInternalTypeBase runParser(final Reader reader) throws IOException {
		final StreamTokenizer tok = new StreamTokenizer(reader);
		final Tokenizer t = new Tokenizer(tok);
		return coreTypeFactory.getParser("ROOT", null).getState(t);
	}

	/**
	 *
	 * @param is
	 * @return
	 * @throws IOException
	 */
	public static DataQuery parseStream(final InputStream is) throws IOException {
		final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
		final ParserInternalTypeBase res = runParser(br);
		return DataQuery.of(res);
	}

	/**
	 *
	 * @param filePath
	 * @return
	 * @throws IOException
	 */
	public static DataQuery loadFile(final URI filePath) throws IOException {
		return parseStream(filePath.toURL().openStream());
	}

	/**
	 *
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static DataQuery loadFile(final Path filePath) throws IOException, URISyntaxException {
		final String ts = String.join("/", filePath.toString().split("\\\\"));
		return loadFile(ts);
	}

	/**
	 *
	 * @param filePath
	 * @return
	 * @throws IOException
	 * @throws URISyntaxException
	 */
	public static DataQuery loadFile(final String filePath) throws IOException, URISyntaxException {
		final URL tu = Config.class.getClassLoader().getResource(filePath);
		final URI temp = tu.toURI();
		return loadFile(temp);
	}

	/**
	 *
	 * @param data
	 * @return
	 * @throws IOException
	 */
	public static DataQuery parseString(final String data) throws IOException {
		return parseStream(IOUtils.toInputStream(data, StandardCharsets.UTF_8));
	}
}
