package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.interfaces.IStateParser;
import com.keildraco.config.states.RootState;
import com.keildraco.config.testsupport.TypeFactoryMockBuilder;

import static com.keildraco.config.Config.EMPTY_TYPE;

public class MiscelanneousTests {
	@Test
	final void testSetFactory() {
		final IStateParser parser = new RootState(new TypeFactoryMockBuilder().create(), null);
		final TypeFactory tf = new TypeFactoryMockBuilder().create();
		parser.setFactory(tf);
		assertEquals(tf, parser.getFactory(), "IStateParser.setFactory() should complete without exceptions and function correctly");
	}

	@Test
	final void testGetParent() {
		final IStateParser parser = new RootState(new TypeFactoryMockBuilder().create(), null);
		assertEquals(EMPTY_TYPE, parser.getParent(), "IStateParser.getParent() on a parser with \"null\" for a parent should return the canonical Empty type");
	}

	@Test
	final void testSetName() {
		final String name = "BLARGH";
		final IStateParser parser = new RootState(new TypeFactoryMockBuilder().create(), null);
		parser.setName(name);
		assertEquals(name, parser.getName(), "IStateParser.setName() should complete without exceptions and function correctly");
	}
}
