package com.keildraco.config.tests.types;

import static com.keildraco.config.interfaces.ParserInternalTypeBase.EMPTY_TYPE;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;
import com.keildraco.config.types.OperationType;

/**
 *
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
final class OperationTypeTest {

	/**
	 *
	 */
	private OperationType testItem = new OperationType(EMPTY_TYPE, "blargh", "foobar");

	/**
	 *
	 * @throws Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.testItem = new OperationType(EMPTY_TYPE, "blargh", "foobar");
		this.testItem.setOperation("!");
	}

	/**
	 *
	 */
	@Test
	void testOperationTypeString() {
		try {
			final OperationType op = new OperationType("OPERATION");
			op.setName("blargh");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instantiating new OperationType");
		}
	}

	/**
	 *
	 */
	@Test
	void testOperationTypeParserInternalTypeBaseString() {
		try {
			final OperationType op = new OperationType(ParserInternalTypeBase.EMPTY_TYPE,
					"OPERATION");
			op.setName("blargh");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Caught exception instantiating new OperationType");
		}
	}

	/**
	 *
	 */
	@Test
	void testGetType() {
		assertEquals(ItemType.OPERATION, this.testItem.getType());
	}

	/**
	 *
	 */
	@Test
	void testAsString() {
		assertEquals("blargh(! foobar)", this.testItem.getValue());
	}

	/**
	 *
	 */
	@Test
	void testSetOperation() {
		try {
			this.testItem.setOperation("!");
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}
}
