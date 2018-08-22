package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.keildraco.config.data.BasicResult;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.types.IdentifierType;
import static com.keildraco.config.Config.EMPTY_TYPE;

class BasicResultTest {

	private static final BasicResult	testValue	= new BasicResult("blargh");
	private static final String			RESULT		= "ident = value";

	@BeforeAll
	static void setUp() throws Exception {
		testValue.addItem(new IdentifierType(testValue, "ident", "value"));
	}

	@Test
	final void testGetValue() {
		assertEquals(RESULT, testValue.getValue(), "test of getValue() on BasicResult");
	}

	@Test
	final void testGetValueRaw() {
		assertEquals(RESULT, testValue.getValueRaw(), "test of getValueRaw() on BasicResult");
	}

	@Test
	final void testGetParent() {
		assertEquals(EMPTY_TYPE, testValue.getParent(), "test of getParent() on BasicResult");
	}
	
	@Test
	final void testGetType() {
		assertEquals(ItemType.BASIC_RESULT, testValue.getType(), "test of getType() on BasicResult");
	}
}
