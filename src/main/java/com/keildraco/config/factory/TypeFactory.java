/**
 * 
 */
package com.keildraco.config.factory;

import java.io.StreamTokenizer;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.keildraco.config.states.IStateParser;
import com.keildraco.config.types.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
public class TypeFactory {

	private Map<ParserInternalTypeBase.ItemType,Class<? extends ParserInternalTypeBase>> typeMap;
	private Map<String, Class<? extends IStateParser>> parserMap;
	
	/**
	 * Private default constructor
	 */
	public TypeFactory() {
		this.typeMap = new ConcurrentHashMap<>();
		this.parserMap = new ConcurrentHashMap<>();
	}
	
	public void registerType(Class<? extends ParserInternalTypeBase> clazz, ParserInternalTypeBase.ItemType type) {
		this.typeMap.put(type, clazz);
	}
	
	private Class<? extends ParserInternalTypeBase> getTypeInternal(ParserInternalTypeBase.ItemType type) {
		return this.typeMap.get(type);
	}
	
	public ParserInternalTypeBase getType(ParserInternalTypeBase parent, String name, String value, ParserInternalTypeBase.ItemType type) {
		Class<? extends ParserInternalTypeBase> clazz = this.getTypeInternal(type);
		
		if(clazz == null) return ParserInternalTypeBase.EmptyType;
		
		Constructor<? extends ParserInternalTypeBase> cons;
		ParserInternalTypeBase rv;
		
		try {
			cons = clazz.getConstructor(ParserInternalTypeBase.class, String.class, String.class);
			rv = cons.newInstance(parent,name,value);
			return rv;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			String mess = String.format("Error getting instance for type: %s\n%s", e.getMessage(), e.getCause());
			System.err.println(mess);
			e.printStackTrace();
			return ParserInternalTypeBase.EmptyType;
		}
	}
	
	public void registerParser(Class<? extends IStateParser> clazz, String name) {
		this.parserMap.put(name, clazz);
	}
	
	private Class<? extends IStateParser> getParserInternal(String name) {
		return this.parserMap.get(name);
	}
	
	@Nullable
	public IStateParser getParser(String name) {
		Class<? extends IStateParser> clazz = this.getParserInternal(name);
		if(clazz == null) return null;
		
		Constructor<? extends IStateParser> cons;
		IStateParser rv;
		
		try {
			cons = clazz.getConstructor(String.class);
			rv = cons.newInstance(name);
			return rv;
		} catch (NoSuchMethodException | SecurityException | InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			String mess = String.format("Error getting instance for type: %s\n%s", e.getMessage(), e.getCause());
			System.err.println(mess);
			e.printStackTrace();
			return null;
		}		
	}
	
	public ParserInternalTypeBase parseTokens(String parserName, ParserInternalTypeBase parent, StreamTokenizer tok) {
		IStateParser parser = this.getParser(parserName);
		if(parser==null) return ParserInternalTypeBase.EmptyType;
		
		parser.setParent(parent);
		return parser.getState(tok);
	}
}
