package com.keildraco.config.testsupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.Maps;

import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IParserState;
import com.keildraco.config.interfaces.IParserType;
import com.keildraco.config.interfaces.IStateParser;

public class TypeFactoryMockBuilder {
	private Map<ItemType, IParserType> types;
	private Map<String, IParserState> states;
	private Map<String, Map<Pair<TokenType,TokenType>, String>> transitions;
	private TypeFactory theMock;
	
	public TypeFactoryMockBuilder() {
		types = Maps.newConcurrentMap();
		states = Maps.newConcurrentMap();
		transitions = Maps.newConcurrentMap();
		theMock = mock(TypeFactory.class);
	}
	
	public TypeFactoryMockBuilder addType(final ItemType type, final IParserType parserType) {
		types.put(type, parserType);
		return this;
	}
	
	public TypeFactoryMockBuilder addState(final String stateName, final IParserState state) {
		states.put(stateName, state);
		return this;
	}
	
	public TypeFactoryMockBuilder addTransition(final String currentState, final TokenType currentToken, final TokenType nextToken, final String newState) {
		Map<Pair<TokenType,TokenType>, String> transition = transitions.getOrDefault(currentState, Maps.newConcurrentMap());
		transition.put(Pair.of(currentToken, nextToken), newState);
		transitions.put(currentState, transition);
		com.keildraco.config.Config.LOGGER.fatal("Added transition (%s -> %s) at %s :: %s", currentState, newState, currentToken, nextToken);
		return this;
	}

	public TypeFactoryMockBuilder addTransitionsReal(IStateParser...parsers) {
		for(IStateParser parser : parsers) {
			TypeFactory t = mock(TypeFactory.class);
			doAnswer( i -> {
				final String currentState = i.getArgument(0);
				final TokenType currentToken = i.getArgument(1);
				final TokenType nextToken = i.getArgument(2);
				final String nextState = i.getArgument(3);
				final Map<Pair<TokenType,TokenType>, String> stateMaps = transitions.getOrDefault(currentState, Maps.newConcurrentMap());
				stateMaps.put(Pair.of(currentToken, nextToken), nextState);
				transitions.put(currentState, stateMaps);
				com.keildraco.config.Config.LOGGER.fatal("Added (real) transition (%s -> %s) at %s :: %s", currentState, nextState, currentToken, nextToken);
				return null;
			}).when(t).registerStateTransition(any(String.class), any(TokenType.class), any(TokenType.class), any(String.class));
			parser.registerTransitions(t);
		}
		return this;
	}
	
	public TypeFactory create() {
		types.entrySet().forEach( ent -> doAnswer(i -> ent.getValue().get(i.getArgument(0), i.getArgument(1), i.getArgument(2))).when(theMock).getType(any(), any(), any(), eq(ent.getKey())) );
		states.entrySet().forEach( ent -> doAnswer(i -> ent.getValue().get()).when(theMock).getParser(eq(ent.getKey()), any()));
		doAnswer( i -> {
			final String currentState = i.getArgument(0);
			final Token currentToken = i.getArgument(1);
			final Token nextToken = i.getArgument(2);
			final Pair<TokenType,TokenType> pairMatch = Pair.of(currentToken.getType(), nextToken.getType());
			final Map<Pair<TokenType,TokenType>, String> stateMaps = transitions.getOrDefault(currentState, Maps.newConcurrentMap());
			final String nextState = stateMaps.getOrDefault(pairMatch, "");
			
			if(stateMaps.isEmpty() || nextState.isEmpty()) {
				throw new UnknownStateException(String.format(
						"Transition state starting at %s with current as %s and next as %s is not known",
						currentState, currentToken.getType(), nextToken.getType()));
			}

			stateMaps.entrySet().forEach( ent -> com.keildraco.config.Config.LOGGER.fatal("--> token state: %s::%s -> %s", ent.getKey().getLeft(), ent.getKey().getRight(), ent.getValue()));
			
			return theMock.getParser(nextState, null);
		}).when(theMock).nextState(any(String.class), any(Token.class), any(Token.class));

		transitions.entrySet().forEach(ent -> {
			com.keildraco.config.Config.LOGGER.fatal("Transition starts %s:", ent.getKey());
			ent.getValue().entrySet().forEach( ent2 -> com.keildraco.config.Config.LOGGER.fatal("\ttoken state: %s::%s -> %s", ent2.getKey().getLeft(), ent2.getKey().getRight(), ent2.getValue()));
		});
		return theMock;
	}
}