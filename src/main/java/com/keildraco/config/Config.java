package com.keildraco.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import com.keildraco.config.data.DataQuery;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.*;
import com.keildraco.config.states.*;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;
import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;

public class Config {
	private static final TypeFactory coreTypeFactory = new TypeFactory();
	private static final List<ParserInternalTypeBase> internalTypes = Arrays.asList(
			new IdentifierType(null, "", ""),
			new ListType(null, "", ""),
			new SectionType(null, "", ""),
			new OperationType(null, "", ""));
	private static final Map<String, Class<? extends IStateParser>> internalParsers = new ConcurrentHashMap<>();
	public static final Logger LOGGER = LogManager.getFormatterLogger("config");

	static {
		internalParsers.put("KEYVALUE", KeyValueParser.class);
		internalParsers.put("LIST", ListParser.class);
		internalParsers.put("OPERATION", OperationParser.class);
		internalParsers.put("SECTION", SectionParser.class);
	}

	private Config() {
		throw new IllegalAccessError("Class not instantiable!");
	}

	public static TypeFactory getFactory() {
		return coreTypeFactory;
	}

	private static void registerParserInternal(final String name, final Class<? extends IStateParser> clazz) {
		coreTypeFactory.registerParser(() -> {
			Constructor<? extends IStateParser> c;
			try {
				c = clazz.getConstructor(TypeFactory.class);
				return c.newInstance(coreTypeFactory);
			} catch (final NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("Exception getting parser instance for %s: %s", name, e.getMessage());
				LOGGER.error(e.getStackTrace());
				return null;
			}
		}, name);
	}

	private static void registerTypeInternal(final ItemType type, final Class<? extends ParserInternalTypeBase> clazz) {
		coreTypeFactory.registerType((parent, name, value) -> {
			Constructor<? extends ParserInternalTypeBase> c;
			try {
				c = clazz.getConstructor(ParserInternalTypeBase.class, String.class, String.class);
				return c.newInstance(parent==null?EmptyType:parent, name, value);
			} catch (final NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				LOGGER.error("Exception getting type instance for %s: %s", name, e.getMessage());
				LOGGER.error(e.getStackTrace());
				return null;
			}
		}, type);
	}

	public static void registerType(final ItemType type, final Class<? extends ParserInternalTypeBase> clazz) {
		registerTypeInternal(type, clazz);
	}

	public static void registerParser(final String name, final Class<? extends IStateParser> clazz) {
		registerParserInternal(name, clazz);
	}

	public static void registerKnownParts() {
		internalTypes.stream().forEach(type -> registerType(type.getType(), type.getClass()));
		internalParsers.entrySet().stream().forEach(ent -> registerParser(ent.getKey(), ent.getValue()));
	}

	public static void reset() {
		coreTypeFactory.reset();
	}

	private static SectionType runParser(final Reader reader) {
		StreamTokenizer tok = new StreamTokenizer(reader);
		tok.commentChar('#');
		tok.wordChars('_', '_');
		tok.wordChars('-', '-');
		tok.slashSlashComments(true);
		tok.slashStarComments(true);
		final ParserInternalTypeBase root = coreTypeFactory.getType(null, "root", "", ItemType.SECTION);
		return (SectionType) coreTypeFactory.getParser("SECTION", root).getState(tok);
	}

	private static FileSystem getFilesystemForURI(final URI uri) throws IOException {
		if (uri.getScheme().equalsIgnoreCase("jar")) return FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
		else return FileSystems.getDefault();
	}

	public static DataQuery loadFile(final URI filePath) throws IOException {
	    final FileSystem fs = getFilesystemForURI(filePath);
	    final Path p = fs.getPath(filePath.getPath().substring(1));
		final BufferedReader br = Files.newBufferedReader(p);
		final SectionType res = runParser(br);
		return DataQuery.of(res);
	}
	
	public static DataQuery loadFile(final Path filePath) throws IOException {
		return loadFile(filePath.toUri());
	}
	
	public static DataQuery loadFile(final String filePath) throws IOException {
		return loadFile(Paths.get(filePath).toUri());
	}

	public static DataQuery parseString(final String data) {
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(data, StandardCharsets.UTF_8));
		return DataQuery.of(runParser(isr));
	}
}
