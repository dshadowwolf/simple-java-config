package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.interfaces.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public final class ParserInternalTypeBaseTest {

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
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testNoParent = new ParserInternalTypeBase("blargh");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + ") caught when not expected");
		}
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(com.keildraco.config.interfaces.ParserInternalTypeBase, java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testEmptyParent = new ParserInternalTypeBase(
					ParserInternalTypeBase.EmptyType, "blargh");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + ") caught when not expected");
		}
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(com.keildraco.config.interfaces.ParserInternalTypeBase, java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseParserInternalTypeBaseStringString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testEmptyParent = new ParserInternalTypeBase(
					ParserInternalTypeBase.EmptyType, "blargh", "blech");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + ") caught when not expected");
		}
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#get(java.lang.String)}.
	 */
	@Test
	public final void testGet() {
		assertEquals(this.testFoobar, this.testItem.get("foobar"));
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		this.testFoobar.addItem(new ParserInternalTypeBase("blargh"));
		assertAll( () -> assertTrue(this.testItem.has("foobar"), "Test Item has child \"foobar\""),
				() -> assertFalse(this.testItem.has("foobar.baz"), "Test Item's child \"foobar\" doesn't have child \"baz\""),
				() -> assertTrue(this.testItem.has("foobar.blargh"), "Test Item's child \"foobar\" has child \"blargh\""),
				() -> assertFalse(this.testItem.has("blargh"), "Test Item doesn't have child \"blargh\""),
				() -> assertFalse(this.testItem.has("blargh.blech"), "Test Item doesn't have child \"blargh\" with child \"blech\""),
				() -> assertFalse(ParserInternalTypeBase.EmptyType.has("Blargh"), "EmptyType always fails has() checks"));
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.INVALID, this.testItem.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#asString()}.
	 */
	@Test
	public final void testAsString() {
		assertEquals("BaseType()", this.testItem.asString());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#toNumber()}.
	 */
	@Test
	public final void testToNumber() {
		assertEquals(Float.NaN, this.testItem.toNumber());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#toBoolean()}.
	 */
	@Test
	public final void testToBoolean() {
		assertEquals(Boolean.FALSE, this.testItem.toBoolean());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#toList()}.
	 */
	@Test
	public final void testToList() {
		assertEquals(Collections.emptyList(), this.testItem.toList());
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#setName(java.lang.String)}.
	 */
	@Test
	public final void testSetName() {
		final ParserInternalTypeBase t = new ParserInternalTypeBase("a");
		t.setName("b");
		assertEquals("b", t.getName());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals("blech", this.testItem.getName());
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
		try {
			this.testItem.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getParent()}.
	 */
	@Test
	public final void testGetParent() {
		assertEquals(ParserInternalTypeBase.EmptyType, this.testItem.getParent());
	}

	@Test
	public final void testEmptyTypeGet() {
		assertNull(ParserInternalTypeBase.EmptyType.get("blargh"));
	}

	@Test
	public final void testEmptyTypeGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.EMPTY,
				ParserInternalTypeBase.EmptyType.getType());
	}

	@Test
	public final void testEmptyTypeAddItem() {
		try {
			ParserInternalTypeBase.EmptyType.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	@Test
	public final void testParserInternalTypeBaseGetNoMember() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("z");
		assertEquals(ParserInternalTypeBase.EmptyType, p.get("blargh"));
	}

	@Test
	public final void testParserInternalTypeBaseGetValue() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("ZZTOP");
		assertEquals("", p.getValue());
	}

	@Test
	public final void testParserInternalTypeBaseGetChildrenEmpty() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("ZZTOP");
		assertEquals(Collections.emptyMap(), p.getChildren());
	}

	@Test
	public final void testParserInternalTypeBaseGetChildrenMembers() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK");
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP");
		p.addItem(q);
		final Map<String, ParserInternalTypeBase> expectBase = new ConcurrentHashMap<>();
		expectBase.put("ZZTOP", q);
		assertEquals(Collections.unmodifiableMap(expectBase), p.getChildren());
	}
	
	@Test
	public final void testParserInternalTypeBaseGetItemLongNone() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK");
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP");
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EmptyType, p.get("ZZTOP.MUZAK"));
	}

	@Test
	public final void testParserInternalTypeBaseGetItemLongValid() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK");
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP");
		p.addItem(q);
		assertEquals(q, p.get("MUZAK.ZZTOP"));
	}

	@Test
	public final void testParserInternalTypeBaseGetItemLongCondTestOne() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK");
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP");
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EmptyType, p.get("ZZTOP.ZZTOP"));
	}
	
	@Test
	public final void testParserInternalTypeBaseGetItemLongCondTestTwo() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK");
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP");
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EmptyType, p.get("MUZAK.MUZAK"));
	}

	@Test
	public final void testParserInternalTypeBaseGetItemLongCondTestThree() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK");
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP");
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EmptyType, p.get("BLARGH.BLECH"));
	}

}
