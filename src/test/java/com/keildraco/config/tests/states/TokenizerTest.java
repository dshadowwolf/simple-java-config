package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keildraco.config.data.Token;
import com.keildraco.config.data.TokenType;
import com.keildraco.config.tokenizer.Tokenizer;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class TokenizerTest {

	private static final String CAUGHT_EXCEPTION = "Caught exception instantiating Tokenizer: ";

	/**
	 *
	 */
	private StreamTokenizer tok;

	/**
	 *
	 * @throws Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		final InputStream is = IOUtils.toInputStream(
				"a b ( c ) d { e ! f ~ g } h = [ i, j, k, -l, ? ] ", StandardCharsets.UTF_8);
		final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		this.tok = new StreamTokenizer(isr);
	}

	/**
	 *
	 */
	@Test
	void testTokenizer() {
		try {
			final Tokenizer t = new Tokenizer(this.tok);
			assertTrue(t.getClass().equals(Tokenizer.class),
					"No exception caught when instantiating tokenizer");
		} catch (final Exception e) {
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testNextToken() {
		try {
			final Tokenizer t = new Tokenizer(this.tok);
			final Token tt = t.nextToken();
			assertAll(
					"t.nextToken() did not throw an exception and returns a TokenType.IDENTIFIER of value \"a\"",
					() -> assertNotNull(tt, "t.nextToken() returned null!"),
					() -> assertEquals(TokenType.IDENTIFIER, tt.getType(), ""),
					() -> assertEquals("a", tt.getValue(), ""));
		} catch (final IOException e) {
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testHasNextTrue() {
		try {
			Tokenizer t = new Tokenizer(this.tok);
			assertTrue(t.hasNext(),
					"t.hasNext() did not throw an exception and returns true when more tokens remain");
		} catch (final Exception e) {
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testHasNextFalse() {
		final InputStream is = IOUtils.toInputStream("a", StandardCharsets.UTF_8);
		final InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8);
		final StreamTokenizer tok2 = new StreamTokenizer(isr);
		try {
			final Tokenizer t = new Tokenizer(tok2);
			t.nextToken();
			assertFalse(t.hasNext(),
					"t.hasNext() did not throw an exception and returns false when no more tokens remain");
		} catch (final Exception e) {
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testPeekToken() {
		try {
			final Tokenizer t = new Tokenizer(this.tok);
			final Token tt = t.peekToken();
			assertAll(
					"t.peekToken() did not throw an exception and returns a TokenType.IDENTIFIER of value \"a\"",
					() -> assertNotNull(tt, "t.nextToken() returned null!"),
					() -> assertEquals(TokenType.IDENTIFIER, tt.getType(), ""),
					() -> assertEquals("b", tt.getValue(), ""));
		} catch (final IOException e) {
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testPushBack() {
		try {
			final Tokenizer t = new Tokenizer(this.tok);
			final Token tt = t.nextToken();
			assertNotNull(tt, "t.nextToken() returned null!");
			t.pushBack(tt);
			assertTrue(true, "t.nextToken() and t.pushBack() did not throw an exception");
		} catch (final Exception e) {
			fail(CAUGHT_EXCEPTION + e);
		}
	}
}
