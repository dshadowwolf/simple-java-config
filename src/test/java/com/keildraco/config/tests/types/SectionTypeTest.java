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
	private IdentifierType kp;
	
	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
		this.root = new SectionType("ROOT");
		this.child = new SectionType(this.root, "CHILD");
		this.kp = new IdentifierType("blargh", "blech");
		this.child.addItem(new IdentifierType("blargh", "foobar"));
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
		assertEquals(true, this.child.has("blargh"));
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
