package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;
import com.keildraco.config.types.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class ListTypeTest {
	private ListType testItem;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public void setUp() throws Exception {
		this.testItem = new ListType("blank");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#get(java.lang.String)}.
	 */
	@Test
	public final void testGet() {
		final ListType l = new ListType("blargh");
		final IdentifierType i = new IdentifierType("test", "nope");
		l.addItem(i);
		assertEquals(i, l.get("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertFalse(this.testItem.has("test"));
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
			final ListType testItem2 = new ListType("blargh");
			testItem2.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#asString()}.
	 */
	@Test
	public final void testAsString() {
		assertEquals("blank = [  ]", this.testItem.asString());
	}

	@Test
	public final void testGetNotThere() {
		assertEquals(ParserInternalTypeBase.EmptyType, this.testItem.get("no_such_item"), "item doesn't exist");
	}

	@Test
	public final void testOtherAsString() {
		final ListType lt = new ListType("");
		assertEquals("[  ]", lt.asString().trim(), "ListType with blank name should return \"[  ]\"");
	}
}
