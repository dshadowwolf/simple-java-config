package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

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

/**
 *
 * @author Daniel Hazelton
 *
 */
final class ItemMatcherTest {

	private static final String BLARGH = "blargh";
	private static final String CAUGHT_EXCEPTION = "Caught exception running loadFile: ";
	private static final String EXCEPTION_GETTING = "Exception getting type instance for %s: %s";
	private static final String MAGIC = "magic";
	private static final String NAME = "name";
	private static final String OPER = "oper";
	private static final String SECTION = "SECTION";
	private static final String VALUE = "value";

	/**
	 *
	 */
	@Test
	void testItemMatcher() {
		try {
			final ParserInternalTypeBase item = new IdentifierType(MAGIC, NAME);
			final ItemMatcher im = new ItemMatcher(item);
			assertNotNull(im, "Able to instantiate an ItemMatcher");
		} catch (final Exception e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}

	/**
	 *
	 */
	@Test
	void testMatches() {
		final ParserInternalTypeBase item = new IdentifierType(MAGIC, NAME);
		final ItemMatcher im = new ItemMatcher(item);
		assertAll("",
				() -> assertTrue(im.matches("magic.name"), "name and value match"),
				() -> assertFalse(im.matches("name.name"), "name doesn't match but value does"),
				() -> assertFalse(im.matches("magic.xyzzy"), "name matches but value doesn't"),
				() -> assertFalse(im.matches("xyzzy.magic"), "neither name or value match"));
	}

	/**
	 *
	 */
	@Test
	void testAlwaysFalseMatcherMatches() {
		assertFalse(ItemMatcher.ALWAYS_FALSE.matches(BLARGH),
				"The AlwaysFalse matcher should only return false");
	}

	/**
	 *
	 */
	@Test
	void testMoreConditionCoverage() {
		try {
			Config.reset();
			Config.registerKnownParts();
			final IStateParser p = Config.getFactory().getParser(SECTION, null);
			final String data = "section { item = value\n listitem = [ alpha, bravo(! charlie), epsilon(~ foobar) ] }";
			final InputStream is = IOUtils.toInputStream(data, StandardCharsets.UTF_8);
			final InputStreamReader br = new InputStreamReader(is, StandardCharsets.UTF_8);
			final StreamTokenizer tok = new StreamTokenizer(br);
			final Tokenizer t = new Tokenizer(tok);
			final ParserInternalTypeBase pb = p.getState(t);
			final ItemMatcher im = new ItemMatcher(pb);
			final ItemMatcher im2 = new ItemMatcher(ParserInternalTypeBase.EMPTY_TYPE);
			final OperationType o = new OperationType(OPER, VALUE);
			o.setOperation(">");
			final ItemMatcher im3 = new ItemMatcher(o);
			assertAll("result is correct",
					() -> assertNotNull(im, "result not null"),
					() -> assertTrue(im.matches("section"), "section match correct"),
					() -> assertTrue(im.matches("section.item.value"), "full item match works"),
					() -> assertTrue(im.matches("section.item"), "item exists/short name match"),
					() -> assertFalse(im.matches("section.I_Dont_Exist"), "item doesn't exist"),
					() -> assertTrue(im.matches("section.listitem.alpha"),
							"section has a list sub-item named \"listitem\" that has a member named \"alpha\""),
					() -> assertFalse(im.matches("section.listitem.bravo.charlie"),
							"operation named \"bravo\" says \"charlie\" shouldn't match"),
					() -> assertTrue(im.matches("section.listitem.bravo.delta"),
							"operation named \"bravo\" should match \"delta\""),
					() -> assertTrue(im.matches("section.listitem.epsilon.foobar"),
							"foobar temp-ignore operation type named epsilon"),
					() -> assertFalse(im.matches("section.listitem.echo.foxtrot"),
							"check for a different code path"),
					() -> assertFalse(im2.matches("blargh"), "EmptyType should match nothing"),
					() -> assertFalse(im.matches("section.item.foobar"),
							"section.item does not have value foobar"),
					() -> assertFalse(im3.matches("oper.value"),
							"invalid/unknown operation - always false"));
		} catch (final IOException | IllegalArgumentException | NoSuchMethodException
				| InstantiationException | IllegalAccessException | InvocationTargetException
				| IllegalParserStateException | UnknownStateException | GenericParseException e) {
			Config.LOGGER.error(EXCEPTION_GETTING, e.toString(),
					e.getMessage());
			Arrays.stream(e.getStackTrace()).forEach(Config.LOGGER::error);
			fail(CAUGHT_EXCEPTION + e);
		}
	}
}
