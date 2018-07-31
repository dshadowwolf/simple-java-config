/**
 * 
 */
package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.util.Collections;

import com.keildraco.config.types.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class ParserInternalTypeBaseTest {
	private ParserInternalTypeBase testItem;
	private ParserInternalTypeBase testFoobar;
	private ParserInternalTypeBase testNesting;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public void setUp() throws Exception {
		this.testItem = new ParserInternalTypeBase("blech");
		this.testFoobar = new ParserInternalTypeBase("foobar");
		this.testItem.addItem(this.testFoobar);
		this.testNesting = new ParserInternalTypeBase("nesting");
		this.testNesting.addItem(this.testFoobar);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#ParserInternalTypeBase(java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testNoParent = new ParserInternalTypeBase("blargh");
			assertTrue(true, "Expected no exception");
		} catch(final Exception e) {
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
			final ParserInternalTypeBase testEmptyParent = new ParserInternalTypeBase(ParserInternalTypeBase.EmptyType, "blargh");
			assertTrue(true, "Expected no exception");
		} catch(final Exception e) {
			fail("Exception ("+e.getMessage()+") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#ParserInternalTypeBase(com.keildraco.config.types.ParserInternalTypeBase, java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseParserInternalTypeBaseStringString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testEmptyParent = new ParserInternalTypeBase(ParserInternalTypeBase.EmptyType, "blargh", "blech");
			assertTrue(true, "Expected no exception");
		} catch(final Exception e) {
			fail("Exception ("+e.getMessage()+") caught when not expected");
		}
	}
	
	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#get(java.lang.String)}.
	 */
	@Test
	public final void testGet() {
		assertEquals(this.testFoobar, this.testItem.get("foobar"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertTrue(this.testItem.has("foobar"), "Test Item has child \"foobar\"");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.INVALID, this.testItem.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#asString()}.
	 */
	@Test
	public final void testAsString() {
		assertEquals("BaseType()", this.testItem.asString());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#toNumber()}.
	 */
	@Test
	public final void testToNumber() {
		assertEquals(Float.NaN, this.testItem.toNumber());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#toBoolean()}.
	 */
	@Test
	public final void testToBoolean() {
		assertEquals(Boolean.FALSE, this.testItem.toBoolean());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#toList()}.
	 */
	@Test
	public final void testToList() {
		assertEquals(Collections.emptyList(), this.testItem.toList());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#setName(java.lang.String)}.
	 */
	@Test
	public final void testSetName() {
		final ParserInternalTypeBase t = new ParserInternalTypeBase("a");
		t.setName("b");
		assertEquals("b", t.getName());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals("blech", this.testItem.getName());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#addItem(com.keildraco.config.types.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
		try {
			this.testItem.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected no exception");
		} catch(final Exception e) {
			fail("Exception ("+e.getMessage()+" :: "+e+") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#getParent()}.
	 */
	@Test
	public final void testGetParent() {
		assertEquals(ParserInternalTypeBase.EmptyType, this.testItem.getParent());
	}
}
