package com.keildraco.config.tests.states;

import static com.keildraco.config.testsupport.SupportClass.runParser;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.OperationParser;
import com.keildraco.config.types.OperationType;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class OperationParserTest {

	private static final String CAUGHT_EXCEPTION = "Caught exception running loadFile: ";
	private static final String EXCEPTION_GETTING = "Exception getting type instance for {}: {}";
	private static final String IDENT = "ident";
	private static final String RESULT_IS_CORRECT = "result is correct";
	private static final String OP = "op";
	private static final String OPERATION = "OPERATION";
	private static final String NAME_IS_CORRECT = "name is correct";
	private static final String RESULT_NOT_NULL = "result not null";
	private static final String VALUE_IS_CORRECT = "value is correct";

	/**
	 *
	 */
	@Test
	void testGetState() {
		try {
			Config.reset();
			Config.registerKnownParts();
			final OperationType opt = (OperationType) runParser("op(! ident)", OPERATION);
			assertAll(RESULT_IS_CORRECT, () -> assertNotNull(opt, RESULT_NOT_NULL),
					() -> assertEquals(OP, opt.getName(), NAME_IS_CORRECT),
					() -> assertEquals(IDENT, opt.getValueRaw(), VALUE_IS_CORRECT),
					() -> assertEquals('!', opt.getOperator(), ""));
		} catch (final IOException | IllegalArgumentException | NoSuchMethodException
				| InstantiationException | IllegalAccessException | InvocationTargetException
				| IllegalParserStateException | UnknownStateException | GenericParseException
				| URISyntaxException e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(), e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testOperationParser() {
		try {
			final TypeFactory tf = new TypeFactory();
			final OperationParser op = new OperationParser(tf, null);
			assertNotNull(op, "Able to instantiate a OperationParser");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
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
			final TypeFactory tf = new TypeFactory();
			final OperationParser op = new OperationParser(tf, null);
			op.registerTransitions(tf);
			assertTrue(true, "was able to register transitions");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testErrorPaths() {
		final String extraInParens = "op(! id ent)";
		final String invalidOperator = "op(< ident)";
		final String noOperator = "op(ident)";
		final String notAnIdentifier = "op(~ id-ent)";
		final String noWork = "";
		assertAll("",
				() -> assertThrows(GenericParseException.class,
						() -> runParser(extraInParens, OPERATION)),
				() -> assertThrows(GenericParseException.class,
						() -> runParser(invalidOperator, OPERATION)),
				() -> assertThrows(GenericParseException.class,
						() -> runParser(noOperator, OPERATION)),
				() -> assertThrows(GenericParseException.class,
						() -> runParser(notAnIdentifier, OPERATION)),
				() -> assertThrows(IllegalParserStateException.class,
						() -> runParser(noWork, OPERATION)));
	}
}
