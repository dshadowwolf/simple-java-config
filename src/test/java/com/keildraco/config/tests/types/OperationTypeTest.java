package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.ParserInternalTypeBase;

import static com.keildraco.config.types.ParserInternalTypeBase.EmptyType;
import static com.keildraco.config.types.ParserInternalTypeBase.ItemType;

@TestInstance(Lifecycle.PER_CLASS)
public class OperationTypeTest {
	private OperationType testItem;
	
	@BeforeAll
	public void setUp() throws Exception {
		this.testItem = new OperationType(EmptyType, "blargh", "foobar");
		this.testItem.setOperation("!");
	}

	@Test
	public final void testOperationTypeString() {
		try {
			@SuppressWarnings("unused")
			OperationType op = new OperationType("OPERATION");
			assertTrue(true, "Expected no exception");
		} catch(Exception e) {
			fail("Caught exception instantiating new OperationType");
		}
	}
	
	@Test
	public final void testOperationTypeParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			OperationType op = new OperationType(ParserInternalTypeBase.EmptyType, "OPERATION");
			assertTrue(true, "Expected no exception");
		} catch(Exception e) {
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
		} catch(Exception e) {
			fail("Exception ("+e.getMessage()+" :: "+e+") caught when not expected");
		}
	}
}
