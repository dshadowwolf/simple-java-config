package com.keildraco.config.tests.data;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.data.ItemMatcher;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.SectionType;

public class ItemMatcherTest {

	@Before
	public void setUp() throws Exception {
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

}
