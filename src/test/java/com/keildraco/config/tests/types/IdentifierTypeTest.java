package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class IdentifierTypeTest {

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
	public final void testGetNoItem() {
		assertEquals(ParserInternalTypeBase.EmptyType, this.testItem.get("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#get(java.lang.String)}.
	 */
	@Test
	public final void testGetHasItem() {
		assertNotEquals(ParserInternalTypeBase.EmptyType, this.testItem.get("value"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#has(java.lang.String)}.
	 */
	@Test
	public final void testHasByName() {
		assertTrue(this.testItem.has("key"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.IDENTIFIER, this.testItem.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#asString()}.
	 */
	@Test
	public final void testAsStringProper() {
		assertEquals("key = value", this.testItem.asString());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#asString()}.
	 */
	@Test
	public final void testAsStringEmpty() {
		IdentifierType l = new IdentifierType("", "value");
		assertEquals("value", l.asString());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#addItem(com.keildraco.config.types.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
		try {
			this.testItem.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception ("+e.getMessage()+" :: "+e+") caught when not expected");
		}
	}

	@Test
	public final void testHasByIdent() {
		assertTrue(this.testItem.has("value"));
	}
}
