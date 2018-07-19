/**
 * 
 */
package com.keildraco.config.tests.types;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.types.*;
/**
 * @author Daniel Hazelton
 *
 */
public class SectionTypeTest {
	private SectionType root;
	private SectionType child;
	private NumberType PI;
	private IdentifierType kp;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.root = new SectionType("ROOT");
		this.child = new SectionType(this.root, "CHILD");
		this.PI = new NumberType("PI", Math.PI);
		this.kp = new IdentifierType("blargh", "blech");
		this.child.addItem(this.PI);
		this.root.addItem(this.kp);
		this.root.addItem(this.child);
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	/**
	 * Test method for {@link com.keildraco.config.types.SectionType#get(java.lang.String)}.
	 */
	@Test
	public final void testGet() {
		assertEquals((SectionType)this.root.get("CHILD"), this.child);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.SectionType#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertEquals(this.child.has("PI"), true);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.SectionType#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(this.root.getType(), ParserInternalTypeBase.ItemType.SECTION);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.SectionType#addItem(com.keildraco.config.types.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
		try {
			SectionType testItem2 = new SectionType("blargh");
			testItem2.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue("Expected no exception", true);
		} catch(Exception e) {
			fail("Exception ("+e.getMessage()+" :: "+e+") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ParserInternalTypeBase#getParent()}.
	 */
	@Test
	public final void testGetParent() {
		assertEquals(this.child.getParent(), this.root);
	}

}
