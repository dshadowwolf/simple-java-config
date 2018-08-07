package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.interfaces.ItemType;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.SectionType;
import static com.keildraco.config.Config.EMPTY_TYPE;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
final class SectionTypeTest {

	private static final String	BLARGH	= "blargh";
	private static final String	BLECH	= "blech";
	private static final String	CHILD	= "CHILD";
	private static final String	FOOBAR	= "foobar";
	private static final String	ROOT	= "ROOT";

	/**
	 *
	 */
	private SectionType root = new SectionType(ROOT);

	/**
	 *
	 */
	private SectionType child = new SectionType(this.root, CHILD);

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeEach
	void setUp() throws Exception {
		this.root = new SectionType(ROOT);
		this.child = new SectionType(this.root, CHILD);
		final IdentifierType kp = new IdentifierType(BLARGH, BLECH);
		this.child.addItem(new IdentifierType(BLARGH, FOOBAR));
		this.root.addItem(kp);
		this.root.addItem(this.child);
	}

	/**
	 * Test method for {@link com.keildraco.config.types.SectionType#getType()}.
	 */
	@Test
	void testGetType() {
		assertEquals(ItemType.SECTION, this.root.getType(), "");
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.types.SectionType#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
	 */
	@Test
	void testAddItem() {
		try {
			assertNotNull(new SectionType(BLARGH), "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getParent()}.
	 */
	@Test
	void testGetParent() {
		assertEquals(this.child.getParent(), this.root, "");
	}

	/**
	 *
	 */
	@Test
	void testAsString() {
		final String result = String.format("blargh = blech%n child {%n blargh = foobar%n}");
		assertAll("", () -> assertEquals(result, this.root.getValue().trim(), ""),
				() -> assertEquals(result, this.root.getValueRaw().trim(), ""));
	}

	/**
	 *
	 */
	@Test
	void testSectionTypeParentNameValue() {
		try {
			final SectionType stOne = new SectionType(EMPTY_TYPE, BLARGH, BLECH);
			final SectionType stTwo = new SectionType(null, BLARGH);
			final SectionType stThree = new SectionType(EMPTY_TYPE, "");
			final String matchVal = String.format("blargh {%n}%n");
			assertAll("", () -> assertEquals(BLARGH, stOne.getName(), ""),
					() -> assertEquals(ROOT, stTwo.getName(), ""),
					() -> assertEquals(ROOT, stThree.getName(), ""),
					() -> assertEquals(matchVal, stOne.getValue(), ""));
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}
}
