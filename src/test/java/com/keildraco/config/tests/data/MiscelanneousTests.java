package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import static com.keildraco.config.Config.EMPTY_TYPE;

public class MiscelanneousTests {

	@BeforeEach
	final void setupEach() {
		try {
			Config.reset();
			Config.registerKnownParts();
		} catch (final NoSuchMethodException | InstantiationException | IllegalAccessException
				| InvocationTargetException e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	@Test
	final void testSetFactory() {
		final IStateParser parser = Config.getFactory().getParser("ROOT", null);
		final TypeFactory tf = new TypeFactory();
		parser.setFactory(tf);
		assertEquals(tf, parser.getFactory(), "");
	}

	@Test
	final void testGetParent() {
		final IStateParser parser = Config.getFactory().getParser("ROOT", null);
		assertEquals(EMPTY_TYPE, parser.getParent(), "");
	}

	@Test
	final void testSetName() {
		final String name = "BLARGH";
		final IStateParser parser = Config.getFactory().getParser("ROOT", null);
		parser.setName(name);
		assertEquals(name, parser.getName(), "");
	}
}
