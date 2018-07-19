/**
 * 
 */
package com.keildraco.config.tests.types;

import static org.junit.Assert.*;

import java.util.Collections;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.types.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
public class ParserInternalTypeBaseTest {
	private ParserInternalTypeBase testItem;
	private ParserInternalTypeBase testFoobar;
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testItem = new ParserInternalTypeBase("blech");
		this.testFoobar = new ParserInternalTypeBase("foobar");
		this.testItem.addItem(this.testFoobar);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#ParserInternalTypeBase(java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			ParserInternalTypeBase testNoParent = new ParserInternalTypeBase("blargh");
			assertTrue("Expected no exception", true);
		} catch(Exception e) {
			fail("Exception ("+e.getMessage()+") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#ParserInternalTypeBase(com.keildraco.config.types.ParserInternalTypeBase, java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			ParserInternalTypeBase testEmptyParent = new ParserInternalTypeBase(ParserInternalTypeBase.EmptyType, "blargh");
			assertTrue("Expected no exception", true);
		} catch(Exception e) {
			fail("Exception ("+e.getMessage()+") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#get(java.lang.String)}.
	 */
	@Test
	public final void testGet() {
		assertEquals(this.testItem.get("foobar"),this.testFoobar);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertTrue(this.testItem.has("foobar"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(this.testItem.getType(), ParserInternalTypeBase.ItemType.INVALID);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#asString()}.
	 */
	@Test
	public final void testAsString() {
		assertEquals(this.testItem.asString(), "BaseType()");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#toNumber()}.
	 */
	@Test
	public final void testToNumber() {
		assertEquals(this.testItem.toNumber(), Float.NaN);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#toBoolean()}.
	 */
	@Test
	public final void testToBoolean() {
		assertEquals(this.testItem.toBoolean(), Boolean.FALSE);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#toList()}.
	 */
	@Test
	public final void testToList() {
		assertEquals(this.testItem.toList(), Collections.emptyList());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#setName(java.lang.String)}.
	 */
	@Test
	public final void testSetName() {
		ParserInternalTypeBase t = new ParserInternalTypeBase("a");
		t.setName("b");
		assertEquals(t.getName(), "b");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals(this.testItem.getName(),"blech");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#addItem(com.keildraco.config.types.ParserInternalTypeBase)}.
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

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#getParent()}.
	 */
	@Test
	public final void testGetParent() {
		assertEquals(this.testItem.getParent(), ParserInternalTypeBase.EmptyType);
	}
}
