package com.keildraco.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StreamTokenizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
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

import com.keildraco.config.data.DataQuery;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.types.*;
import com.keildraco.config.states.*;
import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;
import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;

public class Config {
	private static final TypeFactory coreTypeFactory = new TypeFactory();
	private static final List<ParserInternalTypeBase> internalTypes = (List<ParserInternalTypeBase>) Arrays.asList(
			new IdentifierType(null, "", ""),
			new ListType(null, "", ""), 
			new SectionType(null, "", ""), 
			new OperationType(null, "", ""));
	private static final Map<String, Class<? extends IStateParser>> internalParsers = new ConcurrentHashMap<>();
	
	static {
		internalParsers.put("KEYVALUE", KeyValueParser.class);
		internalParsers.put("LIST", ListParser.class);
		internalParsers.put("OPERATION", OperationParser.class);
		internalParsers.put("SECTION", SectionParser.class);
	}
	
	public static TypeFactory getFactory() {
		return coreTypeFactory;
	}
	
	private static void registerParserInternal(String name, Class<? extends IStateParser> clazz) {
		coreTypeFactory.registerParser(() -> {
			Constructor<? extends IStateParser> c;
			try {
				c = clazz.getConstructor(TypeFactory.class);
				return c.newInstance(coreTypeFactory);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.err.println("Exception getting parser instance for "+name+": "+e.getMessage());
				e.printStackTrace();
				return null;
			}
		}, name);
	}

	private static void registerTypeInternal(ItemType type, Class<? extends ParserInternalTypeBase> clazz) {
		coreTypeFactory.registerType((parent, name, value) -> {
			Constructor<? extends ParserInternalTypeBase> c;
			try {
				c = clazz.getConstructor(ParserInternalTypeBase.class, String.class, String.class);
				return c.newInstance(parent==null?EmptyType:parent, name, value);
			} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				System.err.println("Exception getting parser instance for "+name+": "+e.getMessage());
				e.printStackTrace();
				return null;
			}
		}, type);
	}

	public static void registerType(ItemType type, Class<? extends ParserInternalTypeBase> clazz) {
		registerTypeInternal(type, clazz);
	}
	
	public static void registerParser(String name, Class<? extends IStateParser> clazz) {
		registerParserInternal(name, clazz);
	}

	public static void registerKnownParts() {
		internalTypes.stream().forEach( type -> registerType(type.getType(), type.getClass()));
		internalParsers.entrySet().stream().forEach( ent -> registerParser(ent.getKey(), ent.getValue()));
	}
	
	public static void reset() {
		coreTypeFactory.reset();
	}
	
	private static SectionType runParser(Reader reader) {
		StreamTokenizer tok = new StreamTokenizer(reader);
		tok.commentChar('#');
		tok.wordChars('_', '_');
		tok.wordChars('-', '-');
		tok.slashSlashComments(true);
		tok.slashStarComments(true);
		ParserInternalTypeBase root = coreTypeFactory.getType(null, "root", "", ItemType.SECTION);
		return (SectionType)coreTypeFactory.getParser("SECTION", root).getState(tok);
	}
	
	private static FileSystem getFilesystemForURI(URI uri) throws IOException {
		if(uri.getScheme().equalsIgnoreCase("jar")) return FileSystems.newFileSystem(uri, Collections.<String, Object>emptyMap());
		else return FileSystems.getDefault();
	}
	public static DataQuery LoadFile(URI filePath) throws IOException {
		System.err.println(String.format("LoadFile([URI] %s)", filePath.getPath()));
		FileSystem fs;
		Path p;
		if(filePath.getScheme().equalsIgnoreCase("jar")) {
			fs = getFilesystemForURI(filePath);
			p = fs.getPath(filePath.getPath().substring(1));
		} else {
			System.err.println(filePath.getPath());
			System.err.println(filePath.getPath().substring(1));
			p = Paths.get(filePath.getPath().substring(1));
		}
		BufferedReader br = Files.newBufferedReader(p);
		SectionType res = runParser(br);
		return DataQuery.of(res);
	}
	
	public static DataQuery LoadFile(Path filePath) throws IOException, URISyntaxException {
		System.err.println("LoadFile([PATH] "+filePath.toString()+")");
		return LoadFile(filePath.toUri());
	}
	
	public static DataQuery LoadFile(String filePath) throws IOException, URISyntaxException {
		System.err.println("LoadFile([STRING] "+filePath+")");
		return LoadFile(Paths.get(filePath).toUri());
	}
	
	public static DataQuery parseString(String data) {
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(data, StandardCharsets.UTF_8));
		return DataQuery.of(runParser(isr));
	}
	
}
