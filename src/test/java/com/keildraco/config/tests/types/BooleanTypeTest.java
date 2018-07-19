package com.keildraco.config.tests.types;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.types.BooleanType;
import com.keildraco.config.types.ParserInternalTypeBase;

public class BooleanTypeTest {
	private BooleanType testBaseItem;
	
	@Before
	public void setUp() throws Exception {
		this.testBaseItem = new BooleanType("blech", false);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testGet() {
		assertEquals(this.testBaseItem.get("test"), ParserInternalTypeBase.EmptyType);
	}

	@Test
	public final void testHas() {
		assertEquals(this.testBaseItem.has("test"), false);
	}

	@Test
	public final void testGetType() {
		assertEquals(this.testBaseItem.getType(), ParserInternalTypeBase.ItemType.BOOLEAN);
	}

	@Test
	public final void testToBoolean() {
		assertEquals(this.testBaseItem.toBoolean(), false);
	}

	@Test
	public final void testAddItem() {
		try {
			this.testBaseItem.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue("Expected no exception", true);
		} catch(Exception e) {
			fail("Exception ("+e.getMessage()+" :: "+e+") caught when not expected");
		}
	}

	@Test
	public final void testGetParent() {
		assertEquals(this.testBaseItem.getParent(), ParserInternalTypeBase.EmptyType);
	}

}
