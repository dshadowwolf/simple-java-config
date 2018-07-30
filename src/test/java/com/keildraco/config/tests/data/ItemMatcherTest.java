package com.keildraco.config.tests.data;

import static org.junit.Assert.*;

import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.ItemMatcher;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.SectionType;

public class ItemMatcherTest {
	private ParserInternalTypeBase base;
	
	@Before
	public void setUp() throws Exception {
		Config.registerKnownParts();
		String testString = "section {\nlist = [ alpha, bravo(!charlie), delta]\necho {\nfoxtrot = golf\n}\n}\n\n";
		InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);		
		this.base = Config.getFactory().parseTokens("SECTION", null, t, null);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testItemMatcher() {
		try {
			SectionType s = new SectionType("ROOT");
			s.addItem(new IdentifierType("ident", "value"));
			@SuppressWarnings("unused")
			ItemMatcher m = new ItemMatcher(s);
		} catch(Exception e) {
			fail("Caught exception instantiating an ItemMatcher: "+e);
		}
		assertTrue("No exceptions instantiating an ItemMatcher", true);
	}

	@Test
	public final void testMatches() {
		try {
			ItemMatcher m = new ItemMatcher(new IdentifierType("ident"));
			assertTrue("ItemMatcher returns true", m.matches("ident"));
		} catch(Exception e) {
			fail("Caught exception instantiating an ItemMatcher: "+e);
		}
	}

	@Test
	public final void testMatchList() {
		ItemMatcher m = new ItemMatcher(this.base.get("section.list"));
		assertTrue("list matches 'alpha'", m.matches("alpha"));
	}

	@Test
	public final void testMatchIdentifier() {
		ItemMatcher m = new ItemMatcher(this.base.get("section.echo.foxtrot"));
		assertTrue("Identifer \"foxtrot\" matches 'golf'", m.matches("golf"));
	}
	
	@Test
	public final void testMatchOperation() {
		ItemMatcher m = new ItemMatcher(this.base.get("section.list"));
		assertTrue("Operation matches bravo.hotel but not bravo.charlie", m.matches("bravo.hotel")&&!m.matches("bravo.charlie"));
	}
}
