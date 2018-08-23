package com.keildraco.config.testsupport;

import static com.keildraco.config.data.Constants.NEWLINE_FORMAT_STRING;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;

import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;

import org.mockito.stubbing.Answer;

import com.google.common.collect.Maps;
import com.keildraco.config.data.BasicResult;
import com.keildraco.config.data.Constants;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
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
			
			return typeMockOf(ItemType.LIST, "blargh", "");
		})
		.when(resp).getState(any(Tokenizer.class));
		
		doAnswer( invocation -> "LIST").when(resp).getName();
		
		return resp;
	}
	
	public static IStateParser mockKeyValueParser() {
		IStateParser resp = mock(IStateParser.class);
		
		doAnswer( invocation -> {
			String name = "key";
			Tokenizer t = invocation.getArgument(0);
			if(t.peek().getType() == TokenType.IDENTIFIER && t.peekToken().getType() == TokenType.STORE) {
				name = t.nextToken().getValue(); // consume opening ident
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
			return typeMockOf(ItemType.IDENTIFIER, name, "value");
		})
		.when(resp).getState(any(Tokenizer.class));
		
		doAnswer( invocation -> "KEYVALUE").when(resp).getName();
		
		return resp;
	}
	
	public static IStateParser mockSectionParser() {
		IStateParser resp = mock(IStateParser.class);
		
		doAnswer( invocation -> {
			Tokenizer t = invocation.getArgument(0);
			Token tt = t.nextToken();
			String name = tt.getValue();
			while(t.hasNext() && tt.getType() != TokenType.CLOSE_BRACE) tt = t.nextToken();
			if(!t.hasNext() && tt.getType() != TokenType.CLOSE_BRACE)
				throw new GenericParseException("Early End of Data");
			else
				tt = t.nextToken();
			
			return typeMockOf(ItemType.SECTION, name, "");
		})
		.when(resp).getState(any(Tokenizer.class));
		
		doAnswer( invocation -> "SECTION").when(resp).getName();
		
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
			com.keildraco.config.Config.LOGGER.fatal("%s -- %s -- %s", name, value, op);
			OperationType rv = (OperationType)typeMockOf(ItemType.OPERATION, name, value);
			rv.setOperation(op);
			return rv;
		})
		.when(resp).getState(any(Tokenizer.class));
		
		doAnswer( invocation -> "OPERATION").when(resp).getName();
		
		return resp;
	}
	
	public static ParserInternalTypeBase identifierTypeMock(final String nameValue) {
		ParserInternalTypeBase resp = mock(IdentifierType.class);
		
		doAnswer( i -> nameValue ).when(resp).getValue();
		doAnswer( i -> nameValue ).when(resp).getName();
		doAnswer( i -> nameValue ).when(resp).getValueRaw();
		
		return resp;
	}
	
	public static ParserInternalTypeBase identifierTypeMock(final String name, final String value) {
		ParserInternalTypeBase resp = mock(IdentifierType.class);
		
		doAnswer( i -> String.format("%s = %s", name, value) ).when(resp).getValue();
		doAnswer( i -> name ).when(resp).getName();
		doAnswer( i -> value ).when(resp).getValueRaw();
		
		return resp;
	}

	public static ParserInternalTypeBase typeMockOf(final ItemType type, final String name, final String value) {
		ParserInternalTypeBase resp;// = mock(ParserInternalTypeBase.class);
		Map<String, ParserInternalTypeBase> values = Maps.newConcurrentMap();
		List<String> oper = new ArrayList<>(1);
		
		Answer<Boolean> operAnswer = i -> (value.equals(i.getArgument(0)) && oper.get(0).equals("~")) || (!value.equals(i.getArgument(0)) && oper.get(0).equals("!"));
		Answer<Boolean> setAnswer = i -> {
			String base = ((String)i.getArgument(0)).toLowerCase(Locale.US);
			int index = base.indexOf('.');

			if (index > 0) {
				String itemName = base.substring(0, index);
				String rest = base.substring(index+1);
				
				if (values.containsKey(itemName)) {
					return values.get(itemName).has(rest);
				}
				return false;
			} else if (index == 0) {
				return false; // nominally an error state
			}
			
			return values.containsKey(base) || name.equals(base);
		};
		
		Answer<Boolean> unitAnswer = i -> value.equals(i.getArgument(0));

		Answer<ParserInternalTypeBase> putValueAnswer = i -> {
			ParserInternalTypeBase pitb = (ParserInternalTypeBase)i.getArgument(0);
			String nameX = pitb.getName().toLowerCase(Locale.US);
			values.put(nameX, i.getArgument(0));
			return pitb;
		};

		Answer<ParserInternalTypeBase> getItem = i -> {
			String base = ((String)i.getArgument(0)).toLowerCase(Locale.US);
			int index = base.indexOf('.');

			if (index > 0) {
				String itemName = base.substring(0, index);

				if (values.containsKey(itemName)) {
					return values.get(itemName);
				}
				return com.keildraco.config.Config.EMPTY_TYPE;
			} else if (index == 0) {
				return com.keildraco.config.Config.EMPTY_TYPE; // nominally an error state
			}
			
			if (values.containsKey(base)) {
				return values.get(base);
			}
			
			return com.keildraco.config.Config.EMPTY_TYPE;
		};
		
		Answer<Boolean> usingHasAnswer;
		Answer<String> valueFormatter;
		Answer<String> rawValueFormatter = i -> value;
		
		switch(type) {
			case SECTION:
				resp = mock(SectionType.class);
				valueFormatter =  i -> {
					List<String> valuesValues = values.values().stream().map( val -> val.getValue()).collect(Collectors.toList());		
					return String.format("%s {%n%s%n}", name, String.join("\n", valuesValues.toArray(new String[valuesValues.size()])));
				};
				rawValueFormatter = i -> {
					List<String> valuesValues = values.values().stream().map( val -> val.getValue()).collect(Collectors.toList());		
					return String.format("%s", String.join("\n", valuesValues.toArray(new String[valuesValues.size()])));
				};
				usingHasAnswer = setAnswer;
				doAnswer( putValueAnswer ).when(resp).addItem(any(ParserInternalTypeBase.class));
				doAnswer( getItem ).when(resp).get(any(String.class));
				break;
			case IDENTIFIER:
				resp = mock(IdentifierType.class);
				valueFormatter = i -> String.format("%s = %s", name, value);
				usingHasAnswer = unitAnswer;
				break;
			case LIST:
				resp = mock(ListType.class);
				valueFormatter = i -> {
					List<String> valuesValues = values.values().stream().map( val -> val.getValue()).collect(Collectors.toList());		
					return String.format("[ %s ]", String.join(", ", valuesValues.toArray(new String[valuesValues.size()])));
				};
				rawValueFormatter = valueFormatter;
				usingHasAnswer = setAnswer;
				doAnswer( putValueAnswer ).when(resp).addItem(any(ParserInternalTypeBase.class));
				doAnswer( getItem ).when(resp).get(any(String.class));
				break;
			case OPERATION:
				resp = mock(OperationType.class);
				valueFormatter = i -> String.format("%s(%s %s)", name, oper.get(0), value);
				usingHasAnswer = operAnswer;
				doAnswer( i -> {
					String op = i.getArgument(0);
					oper.clear();
					oper.add(op);
					return null;
				}).when((OperationType)resp).setOperation(any(String.class));
				doAnswer( i -> (int)oper.get(0).charAt(0)).when((OperationType)resp).getOperator();
				break;
			default:
				throw new IllegalArgumentException(String.format("Type value %s not a known, valid type", type.toString()));
		}
		doAnswer( i -> name ).when(resp).getName();
		doAnswer(i -> type).when(resp).getType();
		doAnswer(rawValueFormatter).when(resp).getValueRaw();
		doAnswer(valueFormatter).when(resp).getValue();
		doAnswer(usingHasAnswer).when(resp).has(any(String.class));
		return resp;
	}

	public static BasicResult basicResultMock() {
		BasicResult resp = mock(BasicResult.class);
		Map<String, ParserInternalTypeBase> values = Maps.newConcurrentMap();

		Answer<Boolean> hasAnswer = i -> values.containsKey(((String)i.getArgument(0)).toLowerCase(Locale.US)) || "ROOT".equals(((String)i.getArgument(0)).toLowerCase(Locale.US));
		Answer<ParserInternalTypeBase> putValueAnswer = i -> values.put(((ParserInternalTypeBase)i.getArgument(0)).getName(), i.getArgument(0));

		Answer<ParserInternalTypeBase> getItem = i -> {
			String itemName = i.getArgument(0);
			if(values.containsKey(itemName)) return values.get(itemName);
			else return com.keildraco.config.Config.EMPTY_TYPE;
		};
		Answer<String> value = i -> String.join(String.format(NEWLINE_FORMAT_STRING), values.values().stream()
				.map(val -> val.getValue()).collect(Collectors.toList()));

		doAnswer( i -> "ROOT" ).when(resp).getName();
		doAnswer(i -> ItemType.BASIC_RESULT).when(resp).getType();
		doAnswer(value).when(resp).getValueRaw();
		doAnswer(value).when(resp).getValue();
		doAnswer(hasAnswer).when(resp).has(any(String.class));
		doAnswer( putValueAnswer ).when(resp).addItem(any(ParserInternalTypeBase.class));
		doAnswer( getItem ).when(resp).get(any(String.class));

		return resp;
	}
}