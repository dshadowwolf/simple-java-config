package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import static com.keildraco.config.Config.EMPTY_TYPE;
import static com.keildraco.config.data.Constants.ParserNames.ROOT;

public class MiscelanneousTests {

	@BeforeEach
	final void setupEach() {
		Config.reset();
		Config.registerKnownParts();
	}

	@Test
	final void testSetFactory() {
		final IStateParser parser = Config.getFactory().getParser(ROOT, null);
		final TypeFactory tf = new TypeFactory();
		parser.setFactory(tf);
		assertEquals(tf, parser.getFactory(), "");
	}

	@Test
	final void testGetParent() {
		final IStateParser parser = Config.getFactory().getParser(ROOT, null);
		assertEquals(EMPTY_TYPE, parser.getParent(), "");
	}

	@Test
	final void testSetName() {
		final String name = "BLARGH";
		final IStateParser parser = Config.getFactory().getParser(ROOT, null);
		parser.setName(name);
		assertEquals(name, parser.getName(), "");
	}
}
