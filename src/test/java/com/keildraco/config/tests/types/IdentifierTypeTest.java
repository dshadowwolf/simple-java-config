/**
 * 
 */
package com.keildraco.config.tests.types;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
public class IdentifierTypeTest {
	private IdentifierType testItem;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testItem = new IdentifierType("key", "value");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#get(java.lang.String)}.
	 */
	@Test
	public final void testGet() {
		assertEquals(ParserInternalTypeBase.EmptyType, this.testItem.get("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertEquals(false, this.testItem.has("test"));
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
	public final void testAsString() {
		assertEquals("key = value", this.testItem.asString());

	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#addItem(com.keildraco.config.types.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
		try {
			this.testItem.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue("Expected no exception", true);
		} catch(Exception e) {
			fail("Exception ("+e.getMessage()+" :: "+e+") caught when not expected");
		}
	}

}
