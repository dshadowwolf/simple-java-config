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
		assertEquals(this.testItem.get("test"), ParserInternalTypeBase.EmptyType);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertEquals(this.testItem.has("test"), false);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(this.testItem.getType(), ParserInternalTypeBase.ItemType.LIST);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#toList()}.
	 */
	@Test
	public final void testToList() {
		assertEquals(this.testItem.toList(), Collections.emptyList());
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
