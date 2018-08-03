package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public final class ParserInternalTypeBaseTest {

	/**
	 *
	 */
	private ParserInternalTypeBase testItem;

	/**
	 *
	 */
	private ParserInternalTypeBase testFoobar;

	/**
	 *
	 */
	private ParserInternalTypeBase testNesting;

	/**
	 *
	 * @param name
	 * @return
	 */
	private static ParserInternalTypeBase getInstance(final String name) {
		return new ParserInternalTypeBase(name) {

			@Override
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return "Abstract!";
			}
		};
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @return
	 */
	private static ParserInternalTypeBase getInstance(final ParserInternalTypeBase parent,
			final String name) {
		return new ParserInternalTypeBase(parent, name) {

			@Override
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return "Abstract!";
			}
		};
	}

	/**
	 *
	 * @param parent
	 * @param name
	 * @param value
	 * @return
	 */
	private static ParserInternalTypeBase getInstance(final ParserInternalTypeBase parent,
			final String name, final String value) {
		return new ParserInternalTypeBase(parent, name, value) {

			@Override
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return "Abstract!";
			}
		};
	}

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public void setUp() throws Exception {
		this.testItem = getInstance("blech");
		this.testFoobar = getInstance("foobar");
		this.testItem.addItem(this.testFoobar);
		this.testNesting = getInstance("nesting");
		this.testNesting.addItem(this.testFoobar);
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(java.lang.String)}.
	 */
	@Test
	public void testParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testNoParent = getInstance("blargh");
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
	public void testParserInternalTypeBaseParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testEmptyParent = getInstance(
					ParserInternalTypeBase.EMPTY_TYPE, "blargh");
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
	public void testParserInternalTypeBaseParserInternalTypeBaseStringString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testEmptyParent = getInstance(
					ParserInternalTypeBase.EMPTY_TYPE, "blargh", "blech");
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
	public void testGet() {
		assertAll(() -> assertEquals(this.testFoobar, this.testItem.get("foobar")),
				() -> assertThrows(IllegalArgumentException.class,
						() -> this.testItem.get(".foo")));
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#has(java.lang.String)}.
	 */
	@Test
	public void testHas() {
		this.testFoobar.addItem(getInstance("blargh"));
		assertAll(() -> assertTrue(this.testItem.has("foobar"), "Test Item has child \"foobar\""),
				() -> assertFalse(this.testItem.has("foobar.baz"),
						"Test Item's child \"foobar\" doesn't have child \"baz\""),
				() -> assertTrue(this.testItem.has("foobar.blargh"),
						"Test Item's child \"foobar\" has child \"blargh\""),
				() -> assertFalse(this.testItem.has("blargh"),
						"Test Item doesn't have child \"blargh\""),
				() -> assertFalse(this.testItem.has("blargh.blech"),
						"Test Item doesn't have child \"blargh\" with child \"blech\""));
	}

	/**
	 *
	 */
	@Test
	public void testEmptyType() {
		assertAll(() -> assertEquals("EMPTY", ParserInternalTypeBase.EMPTY_TYPE.getValue()),
				() -> assertEquals("EMPTY", ParserInternalTypeBase.EMPTY_TYPE.getValueRaw()),
				() -> assertEquals(ItemType.EMPTY, ParserInternalTypeBase.EMPTY_TYPE.getType()),
				() -> assertFalse(ParserInternalTypeBase.EMPTY_TYPE.has("Blargh"),
						"EmptyType always fails has() checks"));
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getType()}.
	 */
	@Test
	public void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.INVALID, this.testItem.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getValue()}.
	 */
	@Test
	public void testAsString() {
		assertEquals("Abstract!", this.testItem.getValue());
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#setName(java.lang.String)}.
	 */
	@Test
	public void testSetName() {
		final ParserInternalTypeBase t = getInstance("a");
		t.setName("b");
		assertEquals("b", t.getName());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getName()}.
	 */
	@Test
	public void testGetName() {
		assertEquals("blech", this.testItem.getName());
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
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
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getParent()}.
	 */
	@Test
	public void testGetParent() {
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, this.testItem.getParent());
	}

	/**
	 *
	 */
	@Test
	public void testEmptyTypeGet() {
		assertNull(ParserInternalTypeBase.EMPTY_TYPE.get("blargh"));
	}

	/**
	 *
	 */
	@Test
	public void testEmptyTypeGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.EMPTY,
				ParserInternalTypeBase.EMPTY_TYPE.getType());
	}

	/**
	 *
	 */
	@Test
	public void testEmptyTypeAddItem() {
		try {
			ParserInternalTypeBase.EMPTY_TYPE.addItem(ParserInternalTypeBase.EMPTY_TYPE);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 *
	 */
	@Test
	public void testParserInternalTypeBaseGetNoMember() {
		final ParserInternalTypeBase p = getInstance("z");
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("blargh"));
	}

	/**
	 *
	 */
	@Test
	public void testParserInternalTypeBaseGetValue() {
		final ParserInternalTypeBase p = getInstance("ZZTOP");
		assertEquals("Abstract!", p.getValueRaw());
	}

	/**
	 *
	 */
	@Test
	public void testParserInternalTypeBaseGetChildrenEmpty() {
		final ParserInternalTypeBase p = getInstance("ZZTOP");
		assertEquals(Collections.emptyMap(), p.getChildren());
	}

	/**
	 *
	 */
	@Test
	public void testParserInternalTypeBaseGetChildrenMembers() {
		final ParserInternalTypeBase p = getInstance("MUZAK");
		final ParserInternalTypeBase q = getInstance("ZZTOP");
		p.addItem(q);
		final Map<String, ParserInternalTypeBase> expectBase = new ConcurrentHashMap<>();
		expectBase.put("ZZTOP", q);
		assertEquals(Collections.unmodifiableMap(expectBase), p.getChildren());
	}

	/**
	 *
	 */
	@Test
	public void testParserInternalTypeBaseGetItemLongNone() {
		final ParserInternalTypeBase p = getInstance("MUZAK");
		final ParserInternalTypeBase q = getInstance("ZZTOP");
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("ZZTOP.MUZAK"));
	}

	/**
	 *
	 */
	@Test
	public void testParserInternalTypeBaseGetItemLongValid() {
		final ParserInternalTypeBase p = getInstance("MUZAK");
		final ParserInternalTypeBase q = getInstance("ZZTOP");
		p.addItem(q);
		assertEquals(q, p.get("MUZAK.ZZTOP"));
	}

	/**
	 *
	 */
	@Test
	public void testParserInternalTypeBaseGetItemLongCondTestOne() {
		final ParserInternalTypeBase p = getInstance("MUZAK");
		final ParserInternalTypeBase q = getInstance("ZZTOP");
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("ZZTOP.ZZTOP"));
	}

	/**
	 *
	 */
	@Test
	public void testParserInternalTypeBaseGetItemLongCondTestTwo() {
		final ParserInternalTypeBase p = getInstance("MUZAK");
		final ParserInternalTypeBase q = getInstance("ZZTOP");
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("MUZAK.MUZAK"));
	}

	/**
	 *
	 */
	@Test
	public void testParserInternalTypeBaseGetItemLongCondTestThree() {
		final ParserInternalTypeBase p = getInstance("MUZAK");
		final ParserInternalTypeBase q = getInstance("ZZTOP");
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("BLARGH.BLECH"));
	}
}
