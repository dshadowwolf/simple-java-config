package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.doThrow;


import java.io.IOException;
import java.io.StreamTokenizer;

import com.keildraco.config.Config;
import com.keildraco.config.states.IStateParser;

@TestInstance(Lifecycle.PER_CLASS)
public class IStateParserTests {
	private IStateParser p;

	@BeforeAll
	public void setUp() throws Exception {
		this.p = Config.getFactory().getParser("SECTION", null);
	}

	@BeforeEach
	public void cleanupSettings() throws Exception {
		Config.reset();
		Config.registerKnownParts();
	}

	@Test
	public final void testTT_WORD() {
		String ttword = p.ttypeToString(StreamTokenizer.TT_WORD);
		assertEquals("TT_WORD", ttword);
	}

	@Test
	public final void testTT_EOF() {
		String tteof = p.ttypeToString(StreamTokenizer.TT_EOF);
		assertEquals("TT_EOF", tteof);
	}

	@Test
	public final void testTT_EOL() {
		String tteol = p.ttypeToString(StreamTokenizer.TT_EOL);
		assertEquals("TT_EOL", tteol);
	}

	@Test
	public final void testTT_NUMBER() {
		String ttnumber = p.ttypeToString(StreamTokenizer.TT_NUMBER);
		assertEquals("TT_NUMBER", ttnumber);
	}

	@Test
	public final void testUNKNOWN() {
		String unknown = p.ttypeToString(33);
		assertEquals("UNKNOWN", unknown);
	}

	@Test
	public final void testErrored() {
		p.clearErrors();
		assertEquals(Boolean.FALSE, p.errored());
	}

	@Test
	public final void testNextTokenExceptions() {
		p.clearErrors();
		try {
		StreamTokenizer tok = mock(StreamTokenizer.class);
		doThrow(IOException.class).when(tok).nextToken();
		@SuppressWarnings("unused")
		int z = p.nextToken(tok);
		} catch (Exception e) {
		fail("unexpected exception: "+e.getMessage());
		}
		assertTrue(p.errored(), "parser is in an error state");
	}

	@Test
	public final void testPeekTokenExceptions() {
		p.clearErrors();
		try {
		StreamTokenizer tok = mock(StreamTokenizer.class);
		doThrow(IOException.class).when(tok).nextToken();
		@SuppressWarnings("unused")
		int z = p.peekToken(tok);
		} catch (Exception e) {
		fail("unexpected exception: "+e.getMessage());
		}
		assertTrue(p.errored(), "parser is in an error state");
	}

}
