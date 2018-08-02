package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.ItemMatcher;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.IdentifierType;

class ItemMatcherTest {
	@Test
	final void testItemMatcher() {
		try {
			ParserInternalTypeBase item = new IdentifierType("magic", "name");
			ItemMatcher m = new ItemMatcher(item);
			assertTrue(m!=null, "Able to instantiate an ItemMatcher");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(), e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: "+e);
		}
	}

	@Test
	final void testMatches() {
		ParserInternalTypeBase item = new IdentifierType("magic", "name");
		ItemMatcher m = new ItemMatcher(item);
		assertTrue(m.matches("magic.name"));
	}

}
