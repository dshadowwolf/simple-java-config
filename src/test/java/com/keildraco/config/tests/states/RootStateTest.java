package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.RootState;

class RootStateTest {

	@Test
	final void testRootState() {
		try {
			TypeFactory f = new TypeFactory();
			RootState rs = new RootState(f, null);
			assertTrue(rs != null, "Able to instantiate a RootState");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	@Test
	final void testRegisterTransitions() {
		try {
			TypeFactory f = new TypeFactory();
			RootState rs = new RootState(f, null);
			rs.registerTransitions(f);
			assertTrue(true, "was able to register transitions");
		} catch (Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			java.util.Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

}
