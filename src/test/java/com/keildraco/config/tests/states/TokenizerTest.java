package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.*;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keildraco.config.states.Tokenizer;
import com.keildraco.config.states.Tokenizer.Token;
import com.keildraco.config.states.Tokenizer.TokenType;

class TokenizerTest {
	private StreamTokenizer tok;
	
	@BeforeEach
	void setUp() throws Exception {
		InputStream is = IOUtils.toInputStream("a b ( c ) d { e ! f ~ g } h = [ i, j, k, -l, ? ]", StandardCharsets.UTF_8);
		InputStreamReader isr = new InputStreamReader(is);
		this.tok = new StreamTokenizer(isr);
	}

	@Test
	final void testTokenizer() {
		try {
			@SuppressWarnings("unused")
			Tokenizer t = new Tokenizer(tok);
			assertTrue(true, "No exception caught when instantiating tokenizer");
		} catch(Exception e) {
			fail("Caught exception instantiating Tokenizer: "+e);
		}
	}

	@Test
	final void testNextToken() {
		try {
			Tokenizer t = new Tokenizer(tok);
			Token tt = t.nextToken();
			assertAll("t.nextToken() did not throw an exception and returns a TokenType.IDENTIFIER of value \"a\"",
					() -> tt.getType().equals(TokenType.IDENTIFIER), () -> tt.getValue().equals("a"));
		} catch(Exception e) {
			fail("Caught exception running test: "+e);
		}
	}

	@Test
	final void testHasNextTrue() {
		try {
			Tokenizer t = new Tokenizer(tok);
			assertTrue(t.hasNext(), "t.hasNext() did not throw an exception and returns true when more tokens remain");
		} catch(Exception e) {
			fail("Caught exception running test: "+e);
		}
	}

	@Test
	final void testHasNextFalse() {
		InputStream is = IOUtils.toInputStream("a", StandardCharsets.UTF_8);
		InputStreamReader isr = new InputStreamReader(is);
		StreamTokenizer tok2 = new StreamTokenizer(isr);
		try {
			Tokenizer t = new Tokenizer(tok2);
			@SuppressWarnings("unused")
			Token tt = t.nextToken();
			assertFalse(t.hasNext(), "t.hasNext() did not throw an exception and returns false when no more tokens remain");
		} catch(Exception e) {
			fail("Caught exception running test: "+e);
		}
	}
	
	@Test
	final void testPeekToken() {
		try {
			Tokenizer t = new Tokenizer(tok);
			Token tt = t.peekToken();
			assertAll("t.peekToken() did not throw an exception and returns a TokenType.IDENTIFIER of value \"a\"",
					() -> tt.getType().equals(TokenType.IDENTIFIER), () -> tt.getValue().equals("a"));
		} catch(Exception e) {
			fail("Caught exception running test: "+e);
		}
	}

	@Test
	final void testPushBack() {
		try {
			Tokenizer t = new Tokenizer(tok);
			Token tt = t.nextToken();
			t.pushBack(tt);
			assertTrue(true, "t.nextToken() and t.pushBack() did not throw an exception");
		} catch(Exception e) {
			fail("Caught exception running test: "+e);
		}
	}

}
