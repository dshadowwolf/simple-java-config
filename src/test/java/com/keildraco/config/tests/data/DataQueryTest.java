package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.keildraco.config.types.SectionType;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.data.BasicResult;
import com.keildraco.config.data.DataQuery;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.testsupport.MockSource;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class DataQueryTest {
	private static final String	LOAD_WORKED				= "Load Worked? ";
	private static ParserInternalTypeBase work;
	
	/**
	 *
	 * @throws Exception
	 */
	@BeforeAll
	static void setUp() throws Exception {
		BasicResult base = MockSource.basicResultMock();
		SectionType realRoot = (SectionType)MockSource.typeMockOf(ItemType.SECTION, "section", "");
		IdentifierType magic = (IdentifierType)MockSource.typeMockOf(ItemType.IDENTIFIER, "magic", "xyzzy");		
		realRoot.addItem(magic);
		base.addItem(realRoot);

		work = base;
	}

	/**
	 *
	 */
	@Test
	void testOf() {
		final DataQuery dq = DataQuery.of(work);
		assertNotNull(dq, LOAD_WORKED);
	}

	/**
	 *
	 */
	@Test
	void testMatches() {
		final DataQuery dq = DataQuery.of(work);
		assertTrue(dq.matches("section.magic.xyzzy"), "basic test");
	}
	
	/**
	 *
	 */
	@Test
	void testAlternateCodePaths() {
		final DataQuery dq = DataQuery.of(work);
		assertAll( () -> assertThrows(IllegalArgumentException.class, () -> dq.matches(".section.magic.xyzzy")),
				() -> assertTrue(dq.matches("section")),
				() -> assertFalse(dq.matches("section.blargh")));
	}
}
