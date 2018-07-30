package com.keildraco.config.tests.data;

import static org.junit.Assert.*;

import java.io.IOException;
import java.nio.file.Paths;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.keildraco.config.Config;
import com.keildraco.config.data.DataQuery;
import com.keildraco.config.types.SectionType;

public class DataQueryTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public final void testOf() {
		try {
			DataQuery dq = DataQuery.of((SectionType)new SectionType("ROOT"));
			assertTrue("DataQuery.of() returned non-null", dq != null);
		} catch(Exception e) {
			fail("Exception in call of DataQuery.of(): "+e);
		}
	}

	@Test
	public final void testGet() {
		Config.reset();
		Config.registerKnownParts();
		DataQuery dq;
		try {
			dq = Config.LoadFile(Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg"));
			assertTrue("dq.get(\"section.magic.xyzzy\") is (not) true ("+dq.get("section.magic.xyzzy")+")", dq.get("section.magic.xyzzy"));
		} catch (IOException e) {
			fail("dq.get() caused an exception: "+e);
		}
	}
	
	@Test
	public final void testGetAll() {
		Config.reset();
		Config.registerKnownParts();
		DataQuery dq;
		try {
			dq = Config.LoadFile(Paths.get("src", "main", "resources", "testassets", "base-config-test.cfg"));
			assertTrue("dq.get(\"section.ident3\") is (not) true ("+dq.get("section.ident3")+")", dq.get("section.ident3"));
		} catch (IOException e) {
			fail("dq.get() caused an exception: "+e);
		}		
	}
}
