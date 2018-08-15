package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Deque;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.google.common.collect.Lists;
import com.keildraco.config.Config;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.data.Token;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.OperationParser;
import com.keildraco.config.testsupport.MockSource;
import com.keildraco.config.testsupport.TypeFactoryMockBuilder;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.OperationType;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class OperationParserTest {

	private static final String	CAUGHT_EXCEPTION	= "Caught exception running loadFile: ";
	private static final String	IDENT				= "ident";
	private static final String	RESULT_IS_CORRECT	= "result is correct";
	private static final String	OP					= "op";
	private static final String	NAME_IS_CORRECT		= "name is correct";
	private static final String	RESULT_NOT_NULL		= "result not null";
	private static final String	VALUE_IS_CORRECT	= "value is correct";
	private static TypeFactory typeFactoryMock;
	private static Tokenizer goodDataTokenizerMock;
	private static Tokenizer noDataTokenizerMock;
	private static Tokenizer extraDataTokenizerMock;
	private static Tokenizer badOperDataTokenizerMock;
	private static Tokenizer noOperDataTokenizerMock;
	private static Tokenizer notAnIdentifierDataTokenizerMock;

	@BeforeAll
	static void setupMocks() {
		Deque<Token> goodData = Lists.newLinkedList(Arrays.asList(new Token("op"), new Token("("), new Token("!"), new Token("ident"), new Token(")")));
		Deque<Token> extraData = Lists.newLinkedList(Arrays.asList(new Token("op"), new Token("("), new Token("!"), new Token("ident"), new Token("ent"), new Token(")")));
		Deque<Token> badOperData = Lists.newLinkedList(Arrays.asList(new Token("op"), new Token("("), new Token("<"), new Token("ident"), new Token("ent"), new Token(")")));
		Deque<Token> noOperData = Lists.newLinkedList(Arrays.asList(new Token("op"), new Token("("), new Token("ident"), new Token("ent"), new Token(")")));
		Deque<Token> notAnIdentifierData = Lists.newLinkedList(Arrays.asList(new Token("op"), new Token("("), new Token("!"), new Token("id-ent"), new Token("ent"), new Token(")")));

		typeFactoryMock = new TypeFactoryMockBuilder()
				.addType(ItemType.OPERATION, i -> new OperationType(i.getArgument(0), i.getArgument(1), i.getArgument(2)))
				.create();
		
		goodDataTokenizerMock = MockSource.tokenizerOf(goodData);
		noDataTokenizerMock = MockSource.noDataTokenizer();
		extraDataTokenizerMock = MockSource.tokenizerOf(extraData);
		badOperDataTokenizerMock = MockSource.tokenizerOf(badOperData);
		noOperDataTokenizerMock = MockSource.tokenizerOf(noOperData);
		notAnIdentifierDataTokenizerMock = MockSource.tokenizerOf(notAnIdentifierData);		
	}
		
	/**
	 *
	 */
	@Test
	void testGetState() {
		final OperationParser p = new OperationParser(typeFactoryMock, null);
		final OperationType opt = (OperationType) p.getState(goodDataTokenizerMock);
		assertAll(RESULT_IS_CORRECT, () -> assertNotNull(opt, RESULT_NOT_NULL),
				() -> assertEquals(OP, opt.getName(), NAME_IS_CORRECT),
				() -> assertEquals(IDENT, opt.getValueRaw(), VALUE_IS_CORRECT),
				() -> assertEquals('!', opt.getOperator(), ""));
	}

	/**
	 *
	 */
	@Test
	void testOperationParser() {
		try {
			final OperationParser op = new OperationParser(typeFactoryMock, null);
			assertNotNull(op, "Able to instantiate a OperationParser");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting type instance for {}: {}", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testRegisterTransitions() {
		try {
			final OperationParser op = new OperationParser(typeFactoryMock, null);
			op.registerTransitions(typeFactoryMock);
			assertTrue(true, "was able to register transitions");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting type instance for {}: {}", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testGetStateNoData() {
		final OperationParser op = new OperationParser(typeFactoryMock, null);
		assertThrows(IllegalParserStateException.class, () -> op.getState(noDataTokenizerMock), "Throws exception on no input data");
	}

	/**
	 *
	 */
	@Test
	void testGetStateExtraData() {
		final OperationParser op = new OperationParser(typeFactoryMock, null);
		assertThrows(GenericParseException.class, () -> op.getState(extraDataTokenizerMock), "Throws exception on extra data being inside the parentheses");
	}

	/**
	 *
	 */
	@Test
	void testGetStateBadOperData() {
		final OperationParser op = new OperationParser(typeFactoryMock, null);
		assertThrows(GenericParseException.class, () -> op.getState(badOperDataTokenizerMock), "Throws exception on there being an unknown operator");
	}

	/**
	 *
	 */
	@Test
	void testGetStateNoOperData() {
		final OperationParser op = new OperationParser(typeFactoryMock, null);
		assertThrows(GenericParseException.class, () -> op.getState(noOperDataTokenizerMock), "Throws exception on there not being an operator");
	}

	/**
	 *
	 */
	@Test
	void testGetStateNotAnIdentifierData() {
		final OperationParser op = new OperationParser(typeFactoryMock, null);
		assertThrows(GenericParseException.class, () -> op.getState(notAnIdentifierDataTokenizerMock), "Throws exception on the \"value\" in the parentheses not being an identifier");
	}
}
