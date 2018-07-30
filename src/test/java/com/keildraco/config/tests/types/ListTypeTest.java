/**
 * 
 */
package com.keildraco.config.tests.types;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.types.ListType;
import com.keildraco.config.types.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
public class ListTypeTest {
	private ListType testItem;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testItem = new ListType("blank");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#get(java.lang.String)}.
	 */
	@Test
	public final void testGet() {
		assertEquals(ParserInternalTypeBase.EmptyType, this.testItem.get("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertEquals(false, this.testItem.has("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.LIST, this.testItem.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#toList()}.
	 */
	@Test
	public final void testToList() {
		assertEquals(Collections.emptyList(), this.testItem.toList());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#addItem(com.keildraco.config.types.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
		try {
			ListType testItem2 = new ListType("blargh");
			testItem2.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue("Expected no exception", true);
		} catch(Exception e) {
			fail("Exception ("+e.getMessage()+" :: "+e+") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#asString()}.
	 */
	@Test
	public final void testAsString() {
		assertEquals("blank = [  ]", this.testItem.asString());
	}

}
