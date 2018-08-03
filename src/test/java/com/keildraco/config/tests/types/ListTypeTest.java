package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertAll;
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
import com.keildraco.config.factory.Tokenizer;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.ListType;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public final class ListTypeTest {

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
	public final void testGet() {
		final ListType l = new ListType("blargh");
		final IdentifierType i = new IdentifierType("test", "nope");
		l.addItem(i);
		assertEquals(i, l.get("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		assertFalse(this.testItem.has("test"));
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.LIST, this.testItem.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.types.ListType#toList()}.
	 */
	@Test
	public final void testToList() {
		assertEquals(Collections.emptyList(), this.testItem.toList());
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.types.ListType#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
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
	public final void testGetValue() {
		assertEquals("blank = [  ]", this.testItem.getValue());
	}
	
	@Test
	public final void testOtherBits() {
		assertAll(
				() -> assertEquals(Boolean.FALSE, this.testItem.toBoolean()),
				() -> assertEquals(Float.NaN, this.testItem.toNumber())
				);
	}

	@Test
	public final void testGetNotThere() {
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, this.testItem.get("no_such_item"),
				"item doesn't exist");
	}

	@Test
	public final void testOtherAsString() {
		final ListType lt = new ListType("");
		assertEquals("[  ]", lt.asString().trim(),
				"ListType with blank name should return \"[  ]\"");
	}
	
	@Test
	public final void testListTypeParentName() {
		try {
			ListType lt = new ListType(ParserInternalTypeBase.EMPTY_TYPE, "blargh");
			assertTrue(lt!=null, "constructor works");
		} catch(Exception e) {
			fail("caught exception: "+e);
		}
	}
	
	@Test
	public final void testListTypeParentNameValue() {
		try {
			ListType lt = new ListType(ParserInternalTypeBase.EMPTY_TYPE, "foo", "bar");
			assertTrue(lt!=null, "constructor works");
		} catch(Exception e) {
			fail("caught exception: "+e);
		}
	}

	@Test
	public final void fullAsString() {
		try {
		Config.reset();
		Config.registerKnownParts();
		String data = "[ a, b, c, d, e(! f) ]";
		IStateParser parser = Config.getFactory().getParser("LIST", null);
		InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
		InputStreamReader br = new InputStreamReader(is);
		StreamTokenizer tok = new StreamTokenizer(br);
		Tokenizer t = new Tokenizer(tok);
		ParserInternalTypeBase pitb = parser.getState(t);
		pitb.setName("foobar");
		assertEquals("foobar = [ a, b, c, d, e(! f) ]", pitb.asString());
		} catch(UnknownStateException | IllegalParserStateException | GenericParseException | IOException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			fail("caught exception: "+e);
		}
	}

}
