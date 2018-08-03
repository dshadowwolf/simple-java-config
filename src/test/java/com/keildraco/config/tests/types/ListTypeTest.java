package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.Config;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public final class ListTypeTest {

	/**
	 *
	 */
	private ListType testItem;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public void setUp() throws Exception {
		this.testItem = new ListType("blank");
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#get(java.lang.String)}.
	 */
	@Test
	public void testGet() {
		final ListType l = new ListType("blargh");
		final IdentifierType i = new IdentifierType("test", "nope");
		l.addItem(i);
		assertEquals(i, l.get("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#has(java.lang.String)}.
	 */
	@Test
	public void testHas() {
		assertFalse(this.testItem.has("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#getType()}.
	 */
	@Test
	public void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.LIST, this.testItem.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#getValueAsList()}.
	 */
	@Test
	public void testGetValueAsList() {
		assertEquals(Collections.emptyList(), this.testItem.getValueAsList());
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.types.ListType#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
	 */
	@Test
	public void testAddItem() {
		try {
			final ListType testItem2 = new ListType("blargh");
			testItem2.addItem(ParserInternalTypeBase.EMPTY_TYPE);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getValue()}.
	 */
	@Test
	public void testGetValue() {
		assertEquals("blank = [  ]", this.testItem.getValue());
	}

	/**
	 *
	 */
	@Test
	public void testGetNotThere() {
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, this.testItem.get("no_such_item"),
				"item doesn't exist");
	}

	/**
	 *
	 */
	@Test
	public void testOtherAsString() {
		final ListType lt = new ListType("");
		assertEquals("[  ]", lt.getValue().trim(),
				"ListType with blank name should return \"[  ]\"");
	}

	/**
	 *
	 */
	@Test
	public void testListTypeParentName() {
		try {
			final ListType lt = new ListType(ParserInternalTypeBase.EMPTY_TYPE, "blargh");
			assertTrue(lt != null, "constructor works");
		} catch (final Exception e) {
			fail("caught exception: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	public void testListTypeParentNameValue() {
		try {
			final ListType lt = new ListType(ParserInternalTypeBase.EMPTY_TYPE, "foo", "bar");
			assertTrue(lt != null, "constructor works");
		} catch (final Exception e) {
			fail("caught exception: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	public void fullAsString() {
		try {
			Config.reset();
			Config.registerKnownParts();
			final String data = "[ a, b, c, d, e(! f) ]";
			final IStateParser parser = Config.getFactory().getParser("LIST", null);
			final InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
			final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
			final StreamTokenizer tok = new StreamTokenizer(br);
			final Tokenizer t = new Tokenizer(tok);
			final ParserInternalTypeBase pitb = parser.getState(t);
			pitb.setName("foobar");
			assertEquals("foobar = [ a, b, c, d, e(! f) ]", pitb.getValue());
		} catch (final UnknownStateException | IllegalParserStateException | GenericParseException
				| IOException | NoSuchMethodException | InstantiationException
				| IllegalAccessException | InvocationTargetException e) {
			fail("caught exception: " + e);
		}
	}

}
