package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.BasicResult;
import com.keildraco.config.data.ItemMatcher;
import com.keildraco.config.data.ItemType;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.testsupport.MockSource;
import com.keildraco.config.types.*;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class ItemMatcherTest {

	private static final String	OPER				= "oper";
	private static final String	VALUE				= "value";

	private static ParserInternalTypeBase dataStructure;
	private static ParserInternalTypeBase mainStructure;
	private static BasicResult basicResultStructure;
	
	@BeforeAll
	static void setupTestData() {
		mainStructure = MockSource.typeMockOf(ItemType.SECTION, "section", "");
		ParserInternalTypeBase magic = MockSource.typeMockOf(ItemType.IDENTIFIER, "magic", "xyzzy");
		ParserInternalTypeBase all = MockSource.typeMockOf(ItemType.IDENTIFIER, "all", "ident3");
		ParserInternalTypeBase list = MockSource.typeMockOf(ItemType.IDENTIFIER, "list", "");
		OperationType op = (OperationType) MockSource.typeMockOf(ItemType.OPERATION, "op", "ident");
		op.setOperation("!");
		OperationType op2 = (OperationType) MockSource.typeMockOf(ItemType.OPERATION, "ni", "epsilon");
		op2.setOperation("~");
		ParserInternalTypeBase ident2 = MockSource.typeMockOf(ItemType.IDENTIFIER, "ident2", "");
		ParserInternalTypeBase key = MockSource.typeMockOf(ItemType.LIST, "key", "");
		key.addItem(list);
		key.addItem(op);
		key.addItem(ident2);
		key.addItem(op2);
		ParserInternalTypeBase blech = MockSource.typeMockOf(ItemType.SECTION, "blech", "");
		ParserInternalTypeBase magic2 = MockSource.typeMockOf(ItemType.IDENTIFIER, "magic", "abcd");
		ParserInternalTypeBase magic3 = MockSource.typeMockOf(ItemType.SECTION, "magicX", "");
		ParserInternalTypeBase magic4 = MockSource.typeMockOf(ItemType.IDENTIFIER, "magic", "abcd");
		magic3.addItem(magic4);
		blech.addItem(magic2);
		blech.addItem(magic3);
		mainStructure.addItem(magic);
		mainStructure.addItem(all);
		mainStructure.addItem(key);
		mainStructure.addItem(list);
		mainStructure.addItem(blech);
		
		dataStructure = magic;
		
		basicResultStructure = MockSource.basicResultMock();
		basicResultStructure.addItem(mainStructure);
	}
	
	/**
	 * 
	 */
	@Test
	void testBasicResultMatching() {
		final ItemMatcher im = new ItemMatcher(basicResultStructure);
		
		assertAll( () -> assertTrue(im.matches("section.magic.xyzzy")),
				() -> assertTrue(im.matches("section")),
				() -> assertTrue(im.matches("section.blech.magicX.magic.abcd")),
				() -> assertTrue(im.matches("section.blech.magicX.magic")),
				() -> assertFalse(im.matches("section.blech.magicX.magic.a1b2c3")),
				() -> assertFalse(im.matches("section.blech.magicX.a1b2c3")),
				() -> assertFalse(im.matches("section.blech.magicX.a1b2c3.1234")),
				() -> assertTrue(im.matches("section.blech")),
				() -> assertFalse(im.matches("foo.bar.baz")));
	}
	
	/**
	 *
	 */
	@Test
	void testItemMatcher() {
		final ItemMatcher im = new ItemMatcher(Config.EMPTY_TYPE);
		assertNotNull(im, "Able to instantiate an ItemMatcher");
	}

	/**
	 *
	 */
	@Test
	void testMatches() {
		final ItemMatcher im = new ItemMatcher(dataStructure);
		assertAll("Value matching tests", () -> assertTrue(im.matches("magic.xyzzy"), "name and value match"),
				() -> assertFalse(im.matches("name.xyzzy"), "name doesn't match but value does"),
				() -> assertFalse(im.matches("magic.name"), "name matches but value doesn't"),
				() -> assertFalse(im.matches("xyzzy.magic"), "neither name or value match"));
	}

	/**
	 *
	 */
	@Test
	void testMoreConditionCoverage() {
		final ItemMatcher im = new ItemMatcher(mainStructure);
		final ItemMatcher im2 = new ItemMatcher(Config.EMPTY_TYPE);
		final OperationType o = (OperationType)MockSource.typeMockOf(ItemType.OPERATION, OPER, VALUE);
		o.setOperation(">");
		final ItemMatcher im3 = new ItemMatcher(o);

		assertAll("result is correct", () -> assertNotNull(im, "result not null"),
				() -> assertTrue(im.matches("section"), "section match correct"),
				() -> assertTrue(im.matches("section.magic.xyzzy"), "full item match works"),
				() -> assertTrue(im.matches("section.magic"), "item exists/short name match"),
				() -> assertFalse(im.matches("section.I_Dont_Exist"), "item doesn't exist"),
				() -> assertTrue(im.matches("section.key.list"),
						"section has a list sub-item named \"key\" that has a member named \"list\""),
				() -> assertFalse(im.matches("section.key.op.ident"),
						"operation named \"op\" says \"ident\" shouldn't match"),
				() -> assertTrue(im.matches("section.key.op.delta"),
						"operation named \"op\" should match \"delta\""),
				() -> assertTrue(im.matches("section.key.ni.epsilon"),
						"epsilon temp-ignore operation type named ni"),
				() -> assertFalse(im.matches("section.key.echo"),
						"check for a different code path"),
				() -> assertFalse(im2.matches("blargh"), "EmptyType should match nothing"),
				() -> assertFalse(im.matches("section.blech.foobar"),
						"section.item does not have value foobar"),
				() -> assertFalse(im3.matches("oper.value"),
						"invalid/unknown operation - always false"));
	}
}
