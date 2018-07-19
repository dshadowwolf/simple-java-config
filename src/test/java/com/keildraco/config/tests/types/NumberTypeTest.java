/**
 * 
 */
package com.keildraco.config.tests.types;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import com.keildraco.config.types.NumberType;
import com.keildraco.config.types.ParserInternalTypeBase;

/**
 * @author Daniel Hazelton
 *
 */
public class NumberTypeTest {
	private NumberType testItem;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.testItem = new NumberType("PI", Math.PI);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.keildraco.config.types.NumberType#get(java.lang.String)}.
	 */
	@Test
	public final void testGet() {
		assertEquals(this.testItem.get("test"), ParserInternalTypeBase.EmptyType);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.NumberType#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertEquals(this.testItem.has("test"), false);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.NumberType#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(this.testItem.getType(), ParserInternalTypeBase.ItemType.NUMBER);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.NumberType#toNumber()}.
	 */
	@Test
	public final void testToNumber() {
		assertEquals(this.testItem.toNumber(), Math.PI);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.NumberType#addItem(com.keildraco.config.types.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
		try {
			this.testItem.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue("Expected no exception", true);
		} catch(Exception e) {
			fail("Exception ("+e.getMessage()+" :: "+e+") caught when not expected");
		}
	}

}
