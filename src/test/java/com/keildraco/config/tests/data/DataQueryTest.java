package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.keildraco.config.types.SectionType;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.data.BasicResult;
import com.keildraco.config.data.DataQuery;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

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
		BasicResult base = new BasicResult("ROOT");
		SectionType realRoot = new SectionType(base, "section");
		IdentifierType magic = new IdentifierType(realRoot, "magic", "xyzzy");		
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
		assertTrue(true, "blargh");
		final DataQuery dq = DataQuery.of(work);
		assertTrue(dq.matches("section.magic.xyzzy"), "basic test");
	}
}
