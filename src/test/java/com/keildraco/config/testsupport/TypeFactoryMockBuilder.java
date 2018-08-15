package com.keildraco.config.testsupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;
import org.mockito.stubbing.Answer;

import com.google.common.collect.Maps;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

public class TypeFactoryMockBuilder {
	private Map<ItemType, Answer<ParserInternalTypeBase>> types;
	private Map<String, Answer<IStateParser>> states;
	private Map<String, Map<Pair<TokenType,TokenType>, String>> transitions;
	private TypeFactory theMock;
	
	public TypeFactoryMockBuilder() {
		types = Maps.newConcurrentMap();
		states = Maps.newConcurrentMap();
		transitions = Maps.newConcurrentMap();
		theMock = mock(TypeFactory.class);
	}
	
	public TypeFactoryMockBuilder addType(final ItemType type, final Answer<ParserInternalTypeBase> answer) {
		types.put(type, answer);
		return this;
	}
	
	public TypeFactoryMockBuilder addState(final String stateName, final Answer<IStateParser> answer) {
		states.put(stateName, answer);
		return this;
	}
	
	public TypeFactoryMockBuilder addTransition(final String currentState, final TokenType currentToken, final TokenType nextToken, final String newState) {
		Map<Pair<TokenType,TokenType>, String> transition = transitions.getOrDefault(currentState, Maps.newConcurrentMap());
		transition.put(Pair.of(currentToken, nextToken), newState);
		transitions.put(currentState, transition);
		return this;
	}
	
	public TypeFactory create() {
		types.entrySet().forEach( ent -> doAnswer(ent.getValue()).when(theMock).getType(any(), any(), any(), eq(ent.getKey())) );
		states.entrySet().forEach( ent -> doAnswer(ent.getValue()).when(theMock).getParser(eq(ent.getKey()), any()));
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
			
			return theMock.getParser(nextState, null);
		}).when(theMock).nextState(any(String.class), any(Token.class), any(Token.class));

		return theMock;
	}
}