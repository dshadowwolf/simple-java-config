package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.IdentifierType;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public final class IdentifierTypeTest {

	/**
	 *
	 */
	private IdentifierType testItem;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public void setUp() throws Exception {
		this.testItem = new IdentifierType("key", "value");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#get(java.lang.String)}.
	 */
	@Test
	public void testGetNoItem() {
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, this.testItem.get("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#get(java.lang.String)}.
	 */
	@Test
	public void testGetHasItem() {
		assertNotEquals(ParserInternalTypeBase.EMPTY_TYPE, this.testItem.get("value"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#has(java.lang.String)}.
	 */
	@Test
	public void testHasByName() {
		assertTrue(this.testItem.has("key"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#getType()}.
	 */
	@Test
	public void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.IDENTIFIER, this.testItem.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#getValue()}.
	 */
	@Test
	public void testAsStringProper() {
		assertEquals("key = value", this.testItem.getValue());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#getValue()}.
	 */
	@Test
	public void testAsStringEmpty() {
		final IdentifierType l = new IdentifierType("", "value");
		assertEquals("value", l.getValueRaw());
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.types.IdentifierType#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
	 */
	@Test
	public void testAddItem() {
		try {
			this.testItem.addItem(ParserInternalTypeBase.EMPTY_TYPE);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 *
	 */
	@Test
	public void testHasByIdent() {
		assertTrue(this.testItem.has("value"));
	}
}
