/**
 * 
 */
package com.keildraco.config.factory;

import java.io.StreamTokenizer;
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

	private Map<ParserInternalTypeBase.ItemType,IParserType> typeMap;
	private Map<String, IParserState> parserMap;
	
	/**
	 * Private default constructor
	 */
	public TypeFactory() {
		this.typeMap = new ConcurrentHashMap<>();
		this.parserMap = new ConcurrentHashMap<>();
	}
	
	public void registerType(IParserType lambda, ParserInternalTypeBase.ItemType type) {
		this.typeMap.put(type, lambda);
	}
	
	public ParserInternalTypeBase getType(@Nullable ParserInternalTypeBase parent, String name, String value, ParserInternalTypeBase.ItemType type) {
		return this.typeMap.get(type).get(parent, name, value);
	}
	
	public void registerParser(IParserState parser, String name) {
		this.parserMap.put(name, parser);
	}
	
	@Nullable
	public IStateParser getParser(String parserName, @Nullable ParserInternalTypeBase parent) {
		IParserState parser = this.parserMap.getOrDefault(parserName, null);
		if(parser == null) return null;
		
		return parser.get();
	}
	
	public ParserInternalTypeBase parseTokens(String parserName, @Nullable ParserInternalTypeBase parent, StreamTokenizer tok, String itemName) {
		IStateParser parser = this.getParser(parserName, parent);
		if(parser==null) return ParserInternalTypeBase.EmptyType;
		
		parser.clearErrors();
		parser.setName(itemName);
		ParserInternalTypeBase rv = parser.getState(tok);
		rv.setName(itemName);
		return rv;
	}
	
	public void reset() {
		this.parserMap.clear();
		this.typeMap.clear();
	}
}
