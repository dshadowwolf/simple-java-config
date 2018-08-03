package com.keildraco.config.tests.states;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

import com.keildraco.config.Config;
import com.keildraco.config.factory.TypeFactory;
import com.keildraco.config.states.RootState;

/**
 *
 * @author Daniel Hazelton
 *
 */
class RootStateTest {

	/**
	 *
	 */
	@Test
	final void testRootState() {
		try {
			final TypeFactory f = new TypeFactory();
			final RootState rs = new RootState(f, null);
			assertTrue(rs != null, "Able to instantiate a RootState");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}

	/**
	 *
	 */
	@Test
	final void testRegisterTransitions() {
		try {
			final TypeFactory f = new TypeFactory();
			final RootState rs = new RootState(f, null);
			rs.registerTransitions(f);
			assertTrue(true, "was able to register transitions");
		} catch (final Exception e) {
			Config.LOGGER.error("Exception getting type instance for %s: %s", e.toString(),
					e.getMessage());
			Arrays.asList(e.getStackTrace()).stream().forEach(Config.LOGGER::error);
			fail("Caught exception running loadFile: " + e);
		}
	}
}
