package com.keildraco.config.tests.types;

import static com.keildraco.config.data.Constants.EMPTY_TYPE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.keildraco.config.data.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.testsupport.MockSource;
import com.keildraco.config.types.ListType;

import static com.keildraco.config.Config.EMPTY_TYPE;

/**
 * @author Daniel Hazelton
 *
 */
final class ListTypeTest {

	private static final String	BAR					= "bar";
	private static final String	BLANK				= "blank";
	private static final String	BLARGH				= "blargh";
	private static final String	CAUGHT_EXCEPTION	= "caught exception: ";
	private static final String	CONSTRUCTOR_WORKS	= "constructor works";
	private static final String	FOO					= "foo";
	private static final String	FOOBAR				= "foobar";
	private static final String	NOPE				= "nope";
	private static final String	TEST				= "test";
	
	/**
	 *
	 */
	private static ListType testItem;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	static void setUp() throws Exception {
		testItem = new ListType(BLANK);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#get(java.lang.String)}.
	 */
	@Test
	void testGet() {
		final ListType lt = new ListType(BLARGH);
		final ParserInternalTypeBase idt = MockSource.identifierTypeMock(TEST, NOPE);
		lt.addItem(idt);
		assertEquals(idt, lt.get(TEST), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#has(java.lang.String)}.
	 */
	@Test
	void testHas() {
		assertFalse(testItem.has(TEST), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#getType()}.
	 */
	@Test
	void testGetType() {
		assertEquals(ItemType.LIST, testItem.getType(), "");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#getValueAsList()}.
	 */
	@Test
	void testGetValueAsList() {
		assertEquals(Collections.emptyList(), testItem.getValueAsList(), "");
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.types.ListType#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
	 */
	@Test
	void testAddItem() {
		try {
			final ListType testItem2 = new ListType(BLARGH);
			testItem2.addItem(EMPTY_TYPE);
			assertTrue(testItem2.has(EMPTY_TYPE_NAME), "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getValue()}.
	 */
	@Test
	void testGetValue() {
		assertEquals("blank = [  ]", testItem.getValue(), "");
	}

	/**
	 *
	 */
	@Test
	void testGetNotThere() {
		assertEquals(EMPTY_TYPE, testItem.get("no_such_item"), "item doesn't exist");
	}

	/**
	 *
	 */
	@Test
	void testOtherAsString() {
		final ListType lt = new ListType("");
		assertEquals("[  ]", lt.getValue().trim(),
				"ListType with blank name should return \"[  ]\"");
	}

	/**
	 *
	 */
	@Test
	void testListTypeParentName() {
		try {
			final ListType lt = new ListType(EMPTY_TYPE, BLARGH);
			assertNotNull(lt, CONSTRUCTOR_WORKS);
		} catch (final Exception e) {
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testListTypeParentNameValue() {
		try {
			final ListType lt = new ListType(EMPTY_TYPE, FOO, BAR);
			assertNotNull(lt, CONSTRUCTOR_WORKS);
		} catch (final Exception e) {
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void fullAsString() {
		final ListType test = new ListType(EMPTY_TYPE, FOOBAR, Arrays.asList(MockSource.identifierTypeMock("a"), 
				MockSource.identifierTypeMock("b"), MockSource.identifierTypeMock("c"), 
				MockSource.identifierTypeMock("d"), MockSource.identifierTypeMock("e")));
		assertEquals("foobar = [ a, b, c, d, e ]", test.getValue(), "");
	}
}
