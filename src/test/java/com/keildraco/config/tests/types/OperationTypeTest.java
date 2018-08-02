package com.keildraco.config.tests.types;

import static com.keildraco.config.interfaces.ParserInternalTypeBase.EMPTY_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;
import com.keildraco.config.types.OperationType;

@TestInstance(Lifecycle.PER_CLASS)
public final class OperationTypeTest {

	private OperationType testItem;

	@BeforeAll
	public void setUp() throws Exception {
		this.testItem = new OperationType(EMPTY_TYPE, "blargh", "foobar");
		this.testItem.setOperation("!");
	}

	@Test
	public final void testOperationTypeString() {
		try {
			@SuppressWarnings("unused")
			final OperationType op = new OperationType("OPERATION");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instantiating new OperationType");
		}
	}

	@Test
	public final void testOperationTypeParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			final OperationType op = new OperationType(ParserInternalTypeBase.EMPTY_TYPE,
					"OPERATION");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instantiating new OperationType");
		}
	}

	@Test
	public final void testGetType() {
		assertEquals(ItemType.OPERATION, this.testItem.getType());
	}

	@Test
	public final void testAsString() {
		assertEquals("blargh(! foobar)", this.testItem.asString());
	}

	@Test
	public final void testSetOperation() {
		try {
			this.testItem.setOperation("!");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}
}
