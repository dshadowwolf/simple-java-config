package com.keildraco.config.testsupport;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.Collections;
import java.util.Deque;

import com.keildraco.config.data.Constants;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.SectionType;

public class MockSource {
	public static Tokenizer tokenizerOf(Deque<Token> tokens) {
		Tokenizer resp = mock(Tokenizer.class);
		
		doAnswer( invocation -> {
			if (tokens.isEmpty()) {
				return new Token(Constants.TOKENEMPTY);
			}

			return tokens.peek();
		}).when(resp).peek();

		doAnswer( invocation ->  {
			if ((tokens.isEmpty()) || (tokens.size() == 1)) {
				return new Token(Constants.TOKENEMPTY);
			}

			final Token tok = tokens.pop();
			final Token rv = tokens.peek();

			tokens.push(tok);
			return rv;
		}).when(resp).peekToken();

		doAnswer(invocation -> {
			Token val = invocation.getArgument(0);
			tokens.push(val);
			return null;
		}).when(resp).pushBack(any(Token.class));

		doAnswer(invocation -> !tokens.isEmpty()).when(resp).hasNext();

		doAnswer(invocation -> {
			if (tokens.isEmpty()) {
				return new Token(Constants.TOKENEMPTY);
			}

			Token rv = tokens.pop();

			return rv;
		}).when(resp).nextToken();

		return resp;
	}
	
	public static Tokenizer noDataTokenizer() {
		Tokenizer resp = mock(Tokenizer.class);
		doAnswer(i -> new Token(Constants.TOKENEMPTY)).when(resp).nextToken();
		doAnswer(i -> new Token(Constants.TOKENEMPTY)).when(resp).peek();
		doAnswer(i -> new Token(Constants.TOKENEMPTY)).when(resp).peekToken();
		doAnswer(i -> false).when(resp).hasNext();
		return resp;
	}
	
	public static IStateParser mockListParser() {
		IStateParser resp = mock(IStateParser.class);
		
		doAnswer( invocation -> {
			Tokenizer t = invocation.getArgument(0);
			Token tt = t.nextToken();
			while(t.hasNext() && tt.getType() != TokenType.CLOSE_LIST) tt = t.nextToken();
			if(!t.hasNext() && tt.getType() != TokenType.CLOSE_LIST)
				throw new GenericParseException("Early End of Data");
			else
				tt = t.nextToken();
			
			return new ListType("blargh", Collections.emptyList());
		})
		.when(resp).getState(any(Tokenizer.class));
		
		return resp;
	}
	
	public static IStateParser mockKeyValueParser() {
		IStateParser resp = mock(IStateParser.class);
		
		doAnswer( invocation -> {
			Tokenizer t = invocation.getArgument(0);
			if(t.peek().getType() == TokenType.IDENTIFIER && t.peekToken().getType() == TokenType.STORE) {
				t.nextToken(); // consume opening ident
				t.nextToken(); // consume store
			}
			
			if(t.peek().getType() == TokenType.IDENTIFIER && t.peekToken().getType() == TokenType.OPEN_PARENS) {
				TokenType ntt = t.peek().getType();
				
				while(ntt != TokenType.CLOSE_PARENS && ntt != TokenType.EMPTY)
					ntt = t.nextToken().getType();
				
				if(t.peek().getType() == TokenType.EMPTY)
					throw new GenericParseException("Early End of Data");
				else if(t.peek().getType() == TokenType.CLOSE_PARENS)
					t.nextToken();
			} else if ( t.peek().getType() == TokenType.OPEN_LIST ){
				return mockListParser().getState(t);
			}
			
			t.nextToken(); // consume
			return new IdentifierType("key", "value");
		})
		.when(resp).getState(any(Tokenizer.class));
		
		return resp;
	}
	
	public static IStateParser mockSectionParser() {
		IStateParser resp = mock(IStateParser.class);
		
		doAnswer( invocation -> {
			Tokenizer t = invocation.getArgument(0);
			Token tt = t.nextToken();
			while(t.hasNext() && tt.getType() != TokenType.CLOSE_BRACE) tt = t.nextToken();
			if(!t.hasNext() && tt.getType() != TokenType.CLOSE_BRACE)
				throw new GenericParseException("Early End of Data");
			else
				tt = t.nextToken();
			
			return new SectionType("section");
		})
		.when(resp).getState(any(Tokenizer.class));
		
		return resp;
	}

	public static IStateParser mockOperationParser() {
		IStateParser resp = mock(IStateParser.class);
		
		doAnswer( invocation -> {
			Tokenizer t = invocation.getArgument(0);
			Token ntt = t.peek();
			Token nntt = t.peekToken();
			String name;
			String value;
			String op;
			if( ntt.getType() != TokenType.IDENTIFIER && nntt.getType() != TokenType.OPEN_PARENS) {
				throw new GenericParseException("not an operation!");
			} else {
				name = t.nextToken().getValue(); t.nextToken(); // consume
			}
			ntt = t.peek();
			nntt = t.peekToken();
			if( (ntt.getType() != TokenType.NOT || ntt.getType() != TokenType.TILDE) && nntt.getType() != TokenType.IDENTIFIER) {
				throw new GenericParseException("malformed operation!");
			} else {
				op = t.nextToken().getValue(); value = t.nextToken().getValue(); // consume
			}
			if(t.peek().getType() != TokenType.CLOSE_PARENS) {
				throw new GenericParseException("unclosed operation!");
			}
			
			t.nextToken(); // mass consumption!
			
			OperationType rv = new OperationType(null, name, value);
			rv.setOperation(op);
			return rv;
		})
		.when(resp).getState(any(Tokenizer.class));
		
		return resp;
	}
}