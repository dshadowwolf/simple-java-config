package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.BasicResult;
import com.keildraco.config.data.ItemMatcher;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.types.*;

/**
 *
 * @author Daniel Hazelton
 *
 */
final class ItemMatcherTest {

	private static final String	OPER				= "oper";
	private static final String	VALUE				= "value";

	private static ParserInternalTypeBase fullStructure;
	private static ParserInternalTypeBase sectionOnly;

	@BeforeAll
	static void setupTestData() {
		BasicResult base = new BasicResult("ROOT");
		SectionType realRoot = new SectionType(base, "section");
		IdentifierType magic = new IdentifierType(realRoot, "magic", "xyzzy");		
		IdentifierType all = new IdentifierType(realRoot, "all", "ident3");
		IdentifierType list = new IdentifierType("list");
		OperationType op = new OperationType(Config.EMPTY_TYPE, "op", "ident");
		op.setOperation("!");
		OperationType op2 = new OperationType(Config.EMPTY_TYPE, "ni", "epsilon");
		op2.setOperation("~");
		IdentifierType ident2 = new IdentifierType("ident2");
		ListType key = new ListType(realRoot, "key", Arrays.asList(list, op, ident2, op2));
		SectionType blech = new SectionType(realRoot, "blech");
		IdentifierType magic2 = new IdentifierType(blech, "magic", "abcd");
		blech.addItem(magic2);
		realRoot.addItem(magic);
		realRoot.addItem(all);
		realRoot.addItem(key);
		realRoot.addItem(list);
		realRoot.addItem(blech);
		base.addItem(realRoot);

		fullStructure = base;
		sectionOnly = magic;
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
		final ItemMatcher im = new ItemMatcher(sectionOnly);
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
		final ItemMatcher im = new ItemMatcher(fullStructure);
		final ItemMatcher im2 = new ItemMatcher(Config.EMPTY_TYPE);
		final OperationType o = new OperationType(OPER, VALUE);
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
