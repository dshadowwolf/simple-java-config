package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.data.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;

import static com.keildraco.config.testsupport.SupportClass.getInstance;
import static com.keildraco.config.Config.EMPTY_TYPE;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
final class ParserInternalTypeBaseTest {

	private static final String	ABSTRACT				= "Abstract!";
	private static final String	BLARGH					= "blargh";
	private static final String	BLECH					= "blech";
	private static final String	EMPTY					= "EMPTY";
	private static final String	EXPECTED_NO_EXCEPTION	= "Expected no exception";
	private static final String	FOOBAR					= "foobar";
	private static final String	MUZAK					= "MUZAK";
	private static final String	NESTING					= "nesting";
	private static final String	ZZTOP					= "ZZTOP";

	/**
	 *
	 */
	private ParserInternalTypeBase testItem;

	/**
	 *
	 */
	private ParserInternalTypeBase testFoobar;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	void setUp() throws Exception {
		this.testItem = getInstance(BLECH);
		this.testFoobar = getInstance(FOOBAR);
		this.testItem.addItem(this.testFoobar);
		final ParserInternalTypeBase testNesting = getInstance(NESTING);
		testNesting.addItem(this.testFoobar);
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(java.lang.String)}.
	 */
	@Test
	void testParserInternalTypeBaseString() {
		try {
			assertNotNull(getInstance(BLARGH), EXPECTED_NO_EXCEPTION);
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + ") caught when not expected");
		}
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(com.keildraco.config.interfaces.ParserInternalTypeBase, java.lang.String)}.
	 */
	@Test
	void testParserInternalTypeBaseParserInternalTypeBaseString() {
		try {
			assertNotNull(getInstance(EMPTY_TYPE, BLARGH), EXPECTED_NO_EXCEPTION);
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + ") caught when not expected");
		}
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(com.keildraco.config.interfaces.ParserInternalTypeBase, java.lang.String, java.lang.String)}.
	 */
	@Test
	void testParserInternalTypeBaseParserInternalTypeBaseStringString() {
		try {
			assertNotNull(getInstance(EMPTY_TYPE, BLARGH, BLECH), EXPECTED_NO_EXCEPTION);
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + ") caught when not expected");
		}
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#get(java.lang.String)}.
	 */
	@Test
	void testGet() {
		assertAll("", () -> assertEquals(this.testFoobar, this.testItem.get(FOOBAR), ""),
				() -> assertThrows(IllegalArgumentException.class,
						() -> this.testItem.get(".foo")));
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#has(java.lang.String)}.
	 */
	@Test
	void testHas() {
		this.testFoobar.addItem(getInstance(BLARGH));
		assertAll("", () -> assertTrue(this.testItem.has(FOOBAR), "Test Item has child \"foobar\""),
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
	void testEmptyType() {
		assertAll("", () -> assertEquals(EMPTY, EMPTY_TYPE.getValue(), ""),
				() -> assertEquals(EMPTY, EMPTY_TYPE.getValueRaw(), ""),
				() -> assertEquals(ItemType.EMPTY, EMPTY_TYPE.getType(), ""),
				() -> assertFalse(EMPTY_TYPE.has(BLARGH), "EmptyType always fails has() checks"));
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getType()}.
	 */
	@Test
	void testGetType() {
		assertEquals(ItemType.INVALID, this.testItem.getType(), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getValue()}.
	 */
	@Test
	void testAsString() {
		assertEquals(ABSTRACT, this.testItem.getValue(), "");
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#setName(java.lang.String)}.
	 */
	@Test
	void testSetName() {
		final ParserInternalTypeBase t = getInstance("a");
		t.setName("b");
		assertEquals("b", t.getName(), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getName()}.
	 */
	@Test
	void testGetName() {
		assertEquals(BLECH, this.testItem.getName(), "");
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
	 */
	@Test
	void testAddItem() {
		try {
			this.testItem.addItem(EMPTY_TYPE);
			assertTrue(true, EXPECTED_NO_EXCEPTION);
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getParent()}.
	 */
	@Test
	void testGetParent() {
		assertEquals(EMPTY_TYPE, this.testItem.getParent(), "");
	}

	/**
	 *
	 */
	@Test
	void testEmptyTypeGet() {
		assertEquals(EMPTY_TYPE, EMPTY_TYPE.get(BLARGH), "");
	}

	/**
	 *
	 */
	@Test
	void testEmptyTypeGetType() {
		assertEquals(ItemType.EMPTY, EMPTY_TYPE.getType(), "");
	}

	/**
	 *
	 */
	@Test
	void testEmptyTypeAddItem() {
		try {
			EMPTY_TYPE.addItem(EMPTY_TYPE);
			assertTrue(true, EXPECTED_NO_EXCEPTION);
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetNoMember() {
		final ParserInternalTypeBase p = getInstance("z");
		assertEquals(EMPTY_TYPE, p.get(BLARGH), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetValue() {
		final ParserInternalTypeBase p = getInstance(ZZTOP);
		assertEquals(ABSTRACT, p.getValueRaw(), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetChildrenEmpty() {
		final ParserInternalTypeBase p = getInstance(ZZTOP);
		assertEquals(Collections.emptyMap(), p.getChildren(), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetChildrenMembers() {
		final ParserInternalTypeBase p = getInstance(MUZAK);
		final ParserInternalTypeBase q = getInstance(ZZTOP);
		p.addItem(q);
		final Map<String, ParserInternalTypeBase> expectBase = new ConcurrentHashMap<>();
		expectBase.put(ZZTOP.toLowerCase(Locale.getDefault()), q);
		assertEquals(Collections.unmodifiableMap(expectBase), p.getChildren(), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetItemLongNone() {
		final ParserInternalTypeBase p = getInstance(MUZAK);
		final ParserInternalTypeBase q = getInstance(ZZTOP);
		p.addItem(q);
		assertEquals(EMPTY_TYPE, p.get("ZZTOP.MUZAK"), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetItemLongValid() {
		final ParserInternalTypeBase p = getInstance(MUZAK);
		final ParserInternalTypeBase q = getInstance(ZZTOP);
		p.addItem(q);
		assertEquals(q, p.get("MUZAK.ZZTOP"), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetItemLongCondTestOne() {
		final ParserInternalTypeBase p = getInstance(MUZAK);
		final ParserInternalTypeBase q = getInstance(ZZTOP);
		p.addItem(q);
		assertEquals(EMPTY_TYPE, p.get("ZZTOP.ZZTOP"), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetItemLongCondTestTwo() {
		final ParserInternalTypeBase p = getInstance(MUZAK);
		final ParserInternalTypeBase q = getInstance(ZZTOP);
		p.addItem(q);
		assertEquals(EMPTY_TYPE, p.get("MUZAK.MUZAK"), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetItemLongCondTestThree() {
		final ParserInternalTypeBase p = getInstance(MUZAK);
		final ParserInternalTypeBase q = getInstance(ZZTOP);
		p.addItem(q);
		assertEquals(EMPTY_TYPE, p.get("BLARGH.BLECH"), "");
	}
}
