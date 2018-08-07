package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;

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
final class ParserInternalTypeBaseTest {

	private static final String ABSTRACT = "Abstract!";
	private static final String BLARGH = "blargh";
	private static final String BLECH = "blech";
	private static final String EMPTY = "EMPTY";
	private static final String EXPECTED_NO_EXCEPTION = "Expected no exception";
	private static final String FOOBAR = "foobar";
	private static final String MUZAK = "MUZAK";
	private static final String NESTING = "nesting";
	private static final String ZZTOP = "ZZTOP";

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
	 * @param name
	 * @return
	 */
	private static ParserInternalTypeBase getInstance(final String name) {
		return new ParserInternalTypeBase(name) {

			@Override
			@Nonnull
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return ABSTRACT;
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

			@Nonnull
			@Override
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return ABSTRACT;
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

			@Nonnull
			@Override
			public String getValueRaw() {
				return this.getValue();
			}

			@Override
			public String getValue() {
				return ABSTRACT;
			}
		};
	}

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
			assertNotNull(getInstance(ParserInternalTypeBase.EMPTY_TYPE, BLARGH),
					EXPECTED_NO_EXCEPTION);
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
			assertNotNull(getInstance(ParserInternalTypeBase.EMPTY_TYPE, BLARGH, BLECH),
					EXPECTED_NO_EXCEPTION);
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
		assertAll("", () -> assertEquals(EMPTY, ParserInternalTypeBase.EMPTY_TYPE.getValue(), ""),
				() -> assertEquals(EMPTY, ParserInternalTypeBase.EMPTY_TYPE.getValueRaw(), ""),
				() -> assertEquals(ItemType.EMPTY, ParserInternalTypeBase.EMPTY_TYPE.getType(), ""),
				() -> assertFalse(ParserInternalTypeBase.EMPTY_TYPE.has(BLARGH),
						"EmptyType always fails has() checks"));
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getType()}.
	 */
	@Test
	void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.INVALID, this.testItem.getType(), "");
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
			this.testItem.addItem(ParserInternalTypeBase.EMPTY_TYPE);
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
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, this.testItem.getParent(), "");
	}

	/**
	 *
	 */
	@Test
	void testEmptyTypeGet() {
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE,
				ParserInternalTypeBase.EMPTY_TYPE.get(BLARGH), "");
	}

	/**
	 *
	 */
	@Test
	void testEmptyTypeGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.EMPTY,
				ParserInternalTypeBase.EMPTY_TYPE.getType(), "");
	}

	/**
	 *
	 */
	@Test
	void testEmptyTypeAddItem() {
		try {
			ParserInternalTypeBase.EMPTY_TYPE.addItem(ParserInternalTypeBase.EMPTY_TYPE);
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
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get(BLARGH), "");
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
		expectBase.put(ZZTOP, q);
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
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("ZZTOP.MUZAK"), "");
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
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("ZZTOP.ZZTOP"), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetItemLongCondTestTwo() {
		final ParserInternalTypeBase p = getInstance(MUZAK);
		final ParserInternalTypeBase q = getInstance(ZZTOP);
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("MUZAK.MUZAK"), "");
	}

	/**
	 *
	 */
	@Test
	void testParserInternalTypeBaseGetItemLongCondTestThree() {
		final ParserInternalTypeBase p = getInstance(MUZAK);
		final ParserInternalTypeBase q = getInstance(ZZTOP);
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("BLARGH.BLECH"), "");
	}
}
