package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import java.io.InputStreamReader;
import java.io.StreamTokenizer;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;

import com.keildraco.config.Config;
import com.keildraco.config.data.ItemMatcher;
import com.keildraco.config.types.IdentifierType;
import com.keildraco.config.types.OperationType;
import com.keildraco.config.types.ParserInternalTypeBase;
import com.keildraco.config.types.SectionType;
import com.keildraco.config.types.ParserInternalTypeBase.ItemType;

@TestInstance(Lifecycle.PER_CLASS)
public class ItemMatcherTest {
	private ParserInternalTypeBase base;

	@BeforeAll
	public void setUp() throws Exception {
		Config.registerKnownParts();
		final String testString = "section {\nlist = [ alpha, bravo(!charlie), delta]\necho {\nfoxtrot = golf\n}\n}\n\n";
		final InputStreamReader isr = new InputStreamReader(IOUtils.toInputStream(testString, StandardCharsets.UTF_8));
		final StreamTokenizer t = new StreamTokenizer(isr);
		t.commentChar('#');
		t.wordChars('_', '_');
		t.wordChars('-', '-');
		t.slashSlashComments(true);
		t.slashStarComments(true);
		this.base = Config.getFactory().parseTokens("SECTION", null, t, null);
	}

	@Test
	public final void testItemMatcher() {
		try {
			final SectionType s = new SectionType("ROOT");
			s.addItem(new IdentifierType("ident", "value"));
			@SuppressWarnings("unused")
			final ItemMatcher m = new ItemMatcher(s);
		} catch (final Exception e) {
			fail("Caught exception instantiating an ItemMatcher: "+e);
		}
		assertTrue(true, "No exceptions instantiating an ItemMatcher");
	}

	@Test
	public final void testMatches() {
		try {
			final ItemMatcher m = new ItemMatcher(new IdentifierType("ident"));
			assertTrue(m.matches("ident"), "ItemMatcher returns true");
		} catch (final Exception e) {
			fail("Caught exception instantiating an ItemMatcher: "+e);
		}
	}

	@Test
	public final void testMatchList() {
		final ItemMatcher m = new ItemMatcher(this.base.get("section.list"));
		assertTrue(m.matches("alpha"), "list matches 'alpha'");
	}

	@Test
	public final void testMatchIdentifier() {
		final ItemMatcher m = new ItemMatcher(this.base.get("section.echo.foxtrot"));
		assertTrue(m.matches("golf"), "Identifer \"foxtrot\" matches 'golf'");
	}

	private final boolean noMatch(final ItemMatcher m, final String key) {
		return !m.matches(key);
	}

	@Test
	public final void testMatchOperation() {
		final ItemMatcher m = new ItemMatcher(this.base.get("section.list"));
		assertAll("Operation matches bravo.hotel but not bravo.charlie", () -> m.matches("bravo.hotel"), () -> noMatch(m, "bravo.charlie"));
	}

	@Test
	public final void testMatchSection() {
		final ItemMatcher m = new ItemMatcher(this.base.get("section"));
		assertAll("Section matches \"list\" and \"echo\"", () -> m.matches("list"),() -> m.matches("echo"));
	}

	@Test
	public final void testAlwaysFalseMatcher() {
		assertEquals(Boolean.FALSE, ItemMatcher.AlwaysFalse.matches("bs"));
	}

	@Test
	public final void testMatchOperationSpecificExclude() {
		final OperationType op = (OperationType) Config.getFactory().getType(null, "op", "alpha", ItemType.OPERATION);
		op.setOperation("!");
		final ItemMatcher m = new ItemMatcher(op);
		assertEquals(Boolean.FALSE, m.matches("alpha"));
	}

	@Test
	public final void testMatchOperationSpecificIgnore() {
		final OperationType op = (OperationType) Config.getFactory().getType(null, "op", "alpha", ItemType.OPERATION);
		op.setOperation("~");
		final ItemMatcher m = new ItemMatcher(op);
		assertEquals(Boolean.TRUE, m.matches("alpha"));
	}

	@Test
	public final void testMatchOperationUnknownOperator() {
		final OperationType op = (OperationType) Config.getFactory().getType(null, "op", "alpha", ItemType.OPERATION);
		op.setOperation(">");
		final ItemMatcher m = new ItemMatcher(op);
		assertEquals(Boolean.TRUE, m.matches("alpha"));
	}

	@Test
	public final void testMatchOperationSpecificExcludeLongName() {
		final OperationType op = (OperationType) Config.getFactory().getType(null, "op", "alpha", ItemType.OPERATION);
		op.setOperation("!");
		final ItemMatcher m = new ItemMatcher(op);
		assertEquals(Boolean.FALSE, m.matches("op.alpha"));
	}

	@Test
	public final void testMatchOperationSpecificIgnoreLongName() {
		final OperationType op = (OperationType) Config.getFactory().getType(null, "op", "alpha", ItemType.OPERATION);
		op.setOperation("~");
		final ItemMatcher m = new ItemMatcher(op);
		assertEquals(Boolean.TRUE, m.matches("op.alpha"));
	}

	@Test
	public final void testMatchOperationUnknownOperatorLongName() {
		final OperationType op = (OperationType) Config.getFactory().getType(null, "op", "alpha", ItemType.OPERATION);
		op.setOperation(">");
		final ItemMatcher m = new ItemMatcher(op);
		assertEquals(Boolean.TRUE, m.matches("op.alpha"));
	}
}
