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
		assertEquals(this.testItem.get("test"), ParserInternalTypeBase.EmptyType);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertEquals(this.testItem.has("test"), false);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(this.testItem.getType(), ParserInternalTypeBase.ItemType.IDENTIFIER);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.IdentifierType#asString()}.
	 */
	@Test
	public final void testAsString() {
		assertEquals(this.testItem.asString(), "value");

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
