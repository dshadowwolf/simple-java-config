package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.data.ItemType;
import com.keildraco.config.types.OperationType;
import static com.keildraco.config.Config.EMPTY_TYPE;

/**
 *
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
final class OperationTypeTest {

	private static final String	BLARGH					= "blargh";
	private static final String	CAUGHT_EXCEPTION		= "Caught exception instantiating new OperationType";
	private static final String	EXPECTED_NO_EXCEPTION	= "Expected no exception";
	private static final String	FOOBAR					= "foobar";
	private static final String	OPERATION				= "OPERATION";

	/**
	 *
	 */
	private OperationType testItem = new OperationType(EMPTY_TYPE, BLARGH, FOOBAR);

	/**
	 *
	 * @throws Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.testItem = new OperationType(EMPTY_TYPE, BLARGH, FOOBAR);
		this.testItem.setOperation("!");
	}

	/**
	 *
	 */
	@Test
	void testOperationTypeString() {
		try {
			final OperationType op = new OperationType(OPERATION);
			op.setName(BLARGH);
			assertTrue(true, EXPECTED_NO_EXCEPTION);
		} catch (final Exception e) {
			fail(CAUGHT_EXCEPTION);
		}
	}

	/**
	 *
	 */
	@Test
	void testOperationTypeParserInternalTypeBaseString() {
		try {
			final OperationType op = new OperationType(EMPTY_TYPE, OPERATION);
			op.setName(BLARGH);
			assertTrue(true, EXPECTED_NO_EXCEPTION);
		} catch (final Exception e) {
			fail(CAUGHT_EXCEPTION);
		}
	}

	/**
	 *
	 */
	@Test
	void testGetType() {
		assertEquals(ItemType.OPERATION, this.testItem.getType(), "");
	}

	/**
	 *
	 */
	@Test
	void testAsString() {
		assertEquals("blargh(! foobar)", this.testItem.getValue(), "");
	}

	/**
	 *
	 */
	@Test
	void testSetOperation() {
		try {
			this.testItem.setOperation("!");
			assertTrue(true, EXPECTED_NO_EXCEPTION);
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}
}
