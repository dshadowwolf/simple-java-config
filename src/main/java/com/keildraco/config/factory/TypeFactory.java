package com.keildraco.config.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.interfaces.IParserState;
import com.keildraco.config.interfaces.IParserType;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
public final class TypeFactory {

	private final Map<ParserInternalTypeBase.ItemType, IParserType> typeMap;
	private final Map<String, IParserState> parserMap;
	private final Map<String, Map<TokenType, Map<TokenType, String>>> stateMap;

	/**
	 * Private default constructor.
	 */
	public TypeFactory() {
		this.typeMap = new ConcurrentHashMap<>();
		this.parserMap = new ConcurrentHashMap<>();
		this.stateMap = new ConcurrentHashMap<>();
	}

	public void registerType(final IParserType lambda, final ParserInternalTypeBase.ItemType type) {
		this.typeMap.put(type, lambda);
	}

	public ParserInternalTypeBase getType(@Nullable final ParserInternalTypeBase parent,
			final String name, final String value, final ParserInternalTypeBase.ItemType type) {
		return this.typeMap.get(type).get(parent, name, value);
	}

	public void registerParser(final IParserState parser, final String name) {
		this.parserMap.put(name, parser);
	}

	public void registerStateTransition(final String stateName, final TokenType currentToken,
			final TokenType nextToken, final String toState) {
		Map<TokenType, String> transitionMapping = this.stateMap
				.getOrDefault(stateName, new ConcurrentHashMap<>())
				.getOrDefault(currentToken, new ConcurrentHashMap<>());
		Map<TokenType, Map<TokenType, String>> baseMapping = this.stateMap.getOrDefault(stateName,
				new ConcurrentHashMap<>());
		transitionMapping.put(nextToken, toState);
		baseMapping.put(currentToken, transitionMapping);
		this.stateMap.put(stateName, baseMapping);
	}

	/**
	 *
	 * @param parserName
	 * @param parent
	 * @return
	 */
	@Nullable
	public IStateParser getParser(final String parserName,
			@Nullable final ParserInternalTypeBase parent) {
		final IParserState parser = this.parserMap.getOrDefault(parserName, null);
		if (parser == null) {
			return null;
		}

		return parser.get();
	}

	public IStateParser nextState(final String currentState, final Token currentToken,
			final Token nextToken) throws UnknownStateException {
		String nextState = this.stateMap.getOrDefault(currentState, new ConcurrentHashMap<>())
				.getOrDefault(currentToken.getType(), new ConcurrentHashMap<>())
				.getOrDefault(nextToken.getType(), "");

		if (nextState.length() == 0) {
			throw new UnknownStateException(String.format(
					"Transition state starting at %s with current as %s and next as %s is not known",
					currentState, currentToken.getType(), nextToken.getType()));
		}

		return this.getParser(nextState, null);
	}
}
