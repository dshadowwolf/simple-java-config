package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.types.*;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public class SectionTypeTest {
	private SectionType root;
	private SectionType child;
	private IdentifierType kp;
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public void setUp() throws Exception {
		this.root = new SectionType("ROOT");
		this.child = new SectionType(this.root, "CHILD");
		this.kp = new IdentifierType("blargh", "blech");
		this.child.addItem(new IdentifierType("blargh", "foobar"));
		this.root.addItem(this.kp);
		this.root.addItem(this.child);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.SectionType#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.SECTION, this.root.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.SectionType#addItem(com.keildraco.config.types.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
		try {
			final SectionType testItem2 = new SectionType("blargh");
			testItem2.addItem(ParserInternalTypeBase.EmptyType);
			assertTrue(true, "Expected no exception");
		} catch(final Exception e) {
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
	
	@Test
	public final void testAsString() {
		final String result = String.format("blargh = blech%n" + 
				" CHILD {%n" + 
				" blargh = foobar%n" + 
				"}");
		assertEquals(result, this.root.asString().trim());
	}
}
