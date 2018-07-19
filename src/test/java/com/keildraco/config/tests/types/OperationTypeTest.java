package com.keildraco.config.tests.types;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.types.OperationType;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;
import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;

public class OperationTypeTest {
	private OperationType testItem;
	
	@Before
	public void setUp() throws Exception {
		this.testItem = new OperationType(EmptyType, "blargh", "foobar");
		this.testItem.setOperation("!");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGetType() {
		assertEquals(this.testItem.getType(), ItemType.OPERATION);
	}

	@Test
	public final void testAsString() {
		assertEquals(this.testItem.asString(), "blargh(! foobar)");
	}

	@Test
	public final void testSetOperation() {
		try {
			this.testItem.setOperation("!");
			assertTrue("Expected no exception", true);
		} catch(Exception e) {
			fail("Exception ("+e.getMessage()+" :: "+e+") caught when not expected");
		}
	}

}
