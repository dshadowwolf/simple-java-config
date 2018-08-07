package com.keildraco.config.factory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.ParserRegistrationException;
import com.keildraco.config.exceptions.TypeRegistrationException;
import com.keildraco.config.exceptions.UnknownParseTreeTypeException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.interfaces.IParserState;
import com.keildraco.config.interfaces.IParserType;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import static com.keildraco.config.Config.DEFAULT_HASH_SIZE;

/**
 * @author Daniel Hazelton
 *
 */
public final class TypeFactory {

	/**
	 *
	 */
	private final Map<ItemType, IParserType> typeMap;

	/**
	 *
	 */
	private final Map<String, IParserState> parserMap;

	/**
	 *
	 */
	private final Map<String, Map<TokenType, Map<TokenType, String>>> stateMap;

	/**
	 * Private default constructor.
	 */
	public TypeFactory() {
		this.typeMap = new ConcurrentHashMap<>(DEFAULT_HASH_SIZE);
		this.parserMap = new ConcurrentHashMap<>(DEFAULT_HASH_SIZE);
		this.stateMap = new ConcurrentHashMap<>(DEFAULT_HASH_SIZE);
	}

	/**
	 *
	 */
	public void reset() {
		this.typeMap.clear();
		this.parserMap.clear();
		this.stateMap.clear();
	}

	/**
	 *
	 * @param lambda
	 * @param type
	 */
	public void registerType(final IParserType lambda, final ItemType type) {
		this.typeMap.put(type, lambda);
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param value
	 * @param type
	 * @return
	 */
	public ParserInternalTypeBase getType(@Nullable final ParserInternalTypeBase parent,
			final String name, final String value, final ItemType type) {
		final IParserType ipt = this.typeMap.get(type);

		if (ipt == null) {
			throw new UnknownParseTreeTypeException(
					"Type " + type + " is not registered with the factory");
		}

		final ParserInternalTypeBase rv = ipt.get(parent, name, value);
		if (rv == null) {
			throw new TypeRegistrationException(type);
		}

		return rv;
	}

	/**
	 *
	 * @param parser
	 * @param name
	 */
	public void registerParser(final IParserState parser, final String name) {
		final IStateParser sp = parser.get();
		if (sp != null) {
			sp.registerTransitions(this);
		} else {
			throw new ParserRegistrationException(name);
		}

		this.parserMap.put(name, parser);
	}

	/**
	 *
	 * @param stateName
	 * @param currentToken
	 * @param nextToken
	 * @param toState
	 */
	public void registerStateTransition(final String stateName, final TokenType currentToken,
			final TokenType nextToken, final String toState) {
		final Map<TokenType, String> transitionMapping = this.stateMap
				.getOrDefault(stateName, new ConcurrentHashMap<>(DEFAULT_HASH_SIZE))
				.getOrDefault(currentToken, new ConcurrentHashMap<>(DEFAULT_HASH_SIZE));
		final Map<TokenType, Map<TokenType, String>> baseMapping = this.stateMap
				.getOrDefault(stateName, new ConcurrentHashMap<>(DEFAULT_HASH_SIZE));
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
	public IStateParser getParser(final String parserName,
			@Nullable final ParserInternalTypeBase parent) {
		final IParserState parser = this.parserMap.getOrDefault(parserName, null);
		if (parser == null) {
			throw new UnknownStateException(
					String.format("%s is not a known parser state!", parserName));
		} else {
			final IStateParser rv = parser.get();
			if (rv == null) {
				throw new UnknownStateException(
						"Error getting parser instance: IParserState.get() returned null");
			} else if (parent != null) {
				rv.setParent(parent);
			}
			return rv;
		}
	}

	/**
	 *
	 * @param currentState
	 * @param currentToken
	 * @param nextToken
	 * @return
	 */
	public IStateParser nextState(final String currentState, final Token currentToken,
			final Token nextToken) {
		final String nextState = this.stateMap
				.getOrDefault(currentState, new ConcurrentHashMap<>(DEFAULT_HASH_SIZE))
				.getOrDefault(currentToken.getType(), new ConcurrentHashMap<>(DEFAULT_HASH_SIZE))
				.getOrDefault(nextToken.getType(), "");

		if (nextState.isEmpty()) {
			throw new UnknownStateException(String.format(
					"Transition state starting at %s with current as %s and next as %s is not known",
					currentState, currentToken.getType(), nextToken.getType()));
		}

		return this.getParser(nextState, null);
	}
}
