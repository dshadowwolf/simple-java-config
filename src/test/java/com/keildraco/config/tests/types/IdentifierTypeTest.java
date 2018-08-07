package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.interfaces.ItemType;
import com.keildraco.config.types.IdentifierType;
import static com.keildraco.config.Config.EMPTY_TYPE;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
final class IdentifierTypeTest {

	private static final String EXPECTED_NO_EXCEPTION = "Expected no exception";
	private static final String KEY = "key";
	private static final String VALUE = "value";
	private static final String TEST = "test";

	/**
	 *
	 */
	private IdentifierType testItem;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	void setUp() throws Exception {
		this.testItem = new IdentifierType(KEY, VALUE);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#get(java.lang.String)}.
	 */
	@Test
	void testGetNoItem() {
		assertEquals(EMPTY_TYPE, this.testItem.get(TEST), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#get(java.lang.String)}.
	 */
	@Test
	void testGetHasItem() {
		assertNotEquals(EMPTY_TYPE, this.testItem.get(VALUE), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#has(java.lang.String)}.
	 */
	@Test
	void testHasByName() {
		assertTrue(this.testItem.has(KEY), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#getType()}.
	 */
	@Test
	void testGetType() {
		assertEquals(ItemType.IDENTIFIER, this.testItem.getType(), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#getValue()}.
	 */
	@Test
	void testAsStringProper() {
		assertEquals(KEY + " = " + VALUE, this.testItem.getValue(), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#getValue()}.
	 */
	@Test
	void testAsStringEmpty() {
		final IdentifierType it = new IdentifierType("", VALUE);
		assertEquals(VALUE, it.getValueRaw(), "");
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.types.IdentifierType#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
	 */
	@Test
	void testAddItem() {
		try {
			this.testItem.addItem(EMPTY_TYPE);
			assertTrue(true, EXPECTED_NO_EXCEPTION);
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 *
	 */
	@Test
	void testHasByIdent() {
		assertTrue(this.testItem.has(VALUE), "");
	}
}
