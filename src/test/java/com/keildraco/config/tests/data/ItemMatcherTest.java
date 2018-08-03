package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.ItemMatcher;
import com.keildraco.config.exceptions.GenericParseException;
import com.keildraco.config.exceptions.IllegalParserStateException;
import com.keildraco.config.exceptions.UnknownStateException;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.tokenizer.Tokenizer;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.OperationType;

class ItemMatcherTest {

	@Test
	final void testItemMatcher() {
		try {
			ParserInternalTypeBase item = new IdentifierType("magic", "name");
			ItemMatcher m = new ItemMatcher(item);
			assertTrue(m != null, "Able to instantiate an ItemMatcher");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	@Test
	final void testMatches() {
		ParserInternalTypeBase item = new IdentifierType("magic", "name");
		ItemMatcher m = new ItemMatcher(item);
		assertAll(() -> assertTrue(m.matches("magic.name"), "name and value match"),
				() -> assertFalse(m.matches("name.name"), "name doesn't match but value does"),
				() -> assertFalse(m.matches("magic.xyzzy"), "name matches but value doesn't"),
				() -> assertFalse(m.matches("xyzzy.magic"), "neither name or value match"));
	}

	@Test
	final void testAlwaysFalseMatcherMatches() {
		assertFalse(ItemMatcher.ALWAYS_FALSE.matches("blargh"),
				"The AlwaysFalse matcher should only return false");
	}

	@Test
	final void testMoreConditionCoverage() {
		try {
			Config.reset();
			Config.registerKnownParts();
			IStateParser p = Config.getFactory().getParser("SECTION", null);
			String data = "section { item = value\n listitem = [ alpha, bravo(! charlie), epsilon(~ foobar) ] }";
			InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
			InputStreamReader br = new InputStreamReader(is);
			StreamTokenizer tok = new StreamTokenizer(br);
			Tokenizer t = new Tokenizer(tok);
			ParserInternalTypeBase pb = p.getState(t);
			ItemMatcher m = new ItemMatcher(pb);
			ItemMatcher m2 = new ItemMatcher(ParserInternalTypeBase.EMPTY_TYPE);
			OperationType o = new OperationType("oper", "value");
			o.setOperation(">");
			ItemMatcher m3 = new ItemMatcher(o);
			assertAll("result is correct", () -> assertTrue(m != null, "result not null"),
					() -> assertTrue(m.matches("section"), "section match correct"),
					() -> assertTrue(m.matches("section.item.value"), "full item match works"),
					() -> assertTrue(m.matches("section.item"), "item exists/short name match"),
					() -> assertFalse(m.matches("section.I_Dont_Exist"), "item doesn't exist"),
					() -> assertTrue(m.matches("section.listitem.alpha"),
							"section has a list sub-item named \"listitem\" that has a member named \"alpha\""),
					() -> assertFalse(m.matches("section.listitem.bravo.charlie"),
							"operation named \"bravo\" says \"charlie\" shouldn't match"),
					() -> assertTrue(m.matches("section.listitem.bravo.delta"),
							"operation named \"bravo\" should match \"delta\""),
					() -> assertTrue(m.matches("section.listitem.epsilon.foobar"),
							"foobar temp-ignore operation type named epsilon"),
					() -> assertFalse(m.matches("section.listitem.echo.foxtrot"),
							"check for a different code path"),
					() -> assertFalse(m2.matches("blargh"), "EmptyType should match nothing"),
					() -> assertFalse(m.matches("section.item.foobar"),
							"section.item does not have value foobar"),
					() -> assertFalse(m3.matches("oper.value"),
							"invalid/unknown operation - always false"));
		} catch (final IOException | IllegalArgumentException | NoSuchMethodException
				| InstantiationException | IllegalAccessException | InvocationTargetException
				| IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}
}
