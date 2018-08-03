package com.keildraco.config.tests.types;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;

import com.keildraco.config.interfaces.ParserInternalTypeBase;
import com.keildraco.config.interfaces.ParserInternalTypeBase.ItemType;

/**
 * @author Daniel Hazelton
 *
 */
@TestInstance(Lifecycle.PER_CLASS)
public final class ParserInternalTypeBaseTest {

	private ParserInternalTypeBase testItem;
	private ParserInternalTypeBase testFoobar;
	private ParserInternalTypeBase testNesting;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeAll
	public void setUp() throws Exception {
		this.testItem = new ParserInternalTypeBase("blech") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		this.testFoobar = new ParserInternalTypeBase("foobar") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		this.testItem.addItem(this.testFoobar);
		this.testNesting = new ParserInternalTypeBase("nesting") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		this.testNesting.addItem(this.testFoobar);
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testNoParent = new ParserInternalTypeBase("blargh") {
				@Override
				public String getValue() {
					return "";
				}
				
				@Override
				public String asString() {
					return "Abstract!";
				}
				
				@Override
				public Number toNumber() {
					return Float.NaN;
				}
				
				@Override
				public boolean toBoolean() {
					return Boolean.FALSE;
				}
			};
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + ") caught when not expected");
		}
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(com.keildraco.config.interfaces.ParserInternalTypeBase, java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseParserInternalTypeBaseString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testEmptyParent = new ParserInternalTypeBase(
					ParserInternalTypeBase.EMPTY_TYPE, "blargh") {
				@Override
				public String getValue() {
					return "";
				}
				
				@Override
				public String asString() {
					return "Abstract!";
				}
				
				@Override
				public Number toNumber() {
					return Float.NaN;
				}
				
				@Override
				public boolean toBoolean() {
					return Boolean.FALSE;
				}
			};
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + ") caught when not expected");
		}
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#ParserInternalTypeBase(com.keildraco.config.interfaces.ParserInternalTypeBase, java.lang.String, java.lang.String)}.
	 */
	@Test
	public final void testParserInternalTypeBaseParserInternalTypeBaseStringString() {
		try {
			@SuppressWarnings("unused")
			final ParserInternalTypeBase testEmptyParent = new ParserInternalTypeBase(
					ParserInternalTypeBase.EMPTY_TYPE, "blargh", "blech") {
				@Override
				public String getValue() {
					return "";
				}
				
				@Override
				public String asString() {
					return "Abstract!";
				}
				
				@Override
				public Number toNumber() {
					return Float.NaN;
				}
				
				@Override
				public boolean toBoolean() {
					return Boolean.FALSE;
				}
			};
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + ") caught when not expected");
		}
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#get(java.lang.String)}.
	 */
	@Test
	public final void testGet() {
		assertAll( () -> assertEquals(this.testFoobar, this.testItem.get("foobar")),
				() -> assertThrows(IllegalArgumentException.class, () -> this.testItem.get(".foo")));
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#has(java.lang.String)}.
	 */
	@Test
	public final void testHas() {
		this.testFoobar.addItem(new ParserInternalTypeBase("blargh") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		});
		assertAll( () -> assertTrue(this.testItem.has("foobar"), "Test Item has child \"foobar\""),
				() -> assertFalse(this.testItem.has("foobar.baz"), "Test Item's child \"foobar\" doesn't have child \"baz\""),
				() -> assertTrue(this.testItem.has("foobar.blargh"), "Test Item's child \"foobar\" has child \"blargh\""),
				() -> assertFalse(this.testItem.has("blargh"), "Test Item doesn't have child \"blargh\""),
				() -> assertFalse(this.testItem.has("blargh.blech"), "Test Item doesn't have child \"blargh\" with child \"blech\""));
	}

	@Test
	public final void testEmptyType() {
		assertAll(
				() -> assertEquals("EMPTY", ParserInternalTypeBase.EMPTY_TYPE.getValue()),
				() -> assertEquals(ItemType.EMPTY, ParserInternalTypeBase.EMPTY_TYPE.getType()),
				() -> assertFalse(ParserInternalTypeBase.EMPTY_TYPE.has("Blargh"), "EmptyType always fails has() checks"),
				() -> assertEquals(Boolean.FALSE, ParserInternalTypeBase.EMPTY_TYPE.toBoolean()),
				() -> assertEquals(Float.NaN, ParserInternalTypeBase.EMPTY_TYPE.toNumber())
				);
	}
	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getType()}.
	 */
	@Test
	public final void testGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.INVALID, this.testItem.getType());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#asString()}.
	 */
	@Test
	public final void testAsString() {
		assertEquals("Abstract!", this.testItem.asString());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#toNumber()}.
	 */
	@Test
	public final void testToNumber() {
		assertEquals(Float.NaN, this.testItem.toNumber());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#toBoolean()}.
	 */
	@Test
	public final void testToBoolean() {
		assertEquals(Boolean.FALSE, this.testItem.toBoolean());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#toList()}.
	 */
	@Test
	public final void testToList() {
		assertEquals(Collections.emptyList(), this.testItem.toList());
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#setName(java.lang.String)}.
	 */
	@Test
	public final void testSetName() {
		final ParserInternalTypeBase t = new ParserInternalTypeBase("a") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		t.setName("b");
		assertEquals("b", t.getName());
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getName()}.
	 */
	@Test
	public final void testGetName() {
		assertEquals("blech", this.testItem.getName());
	}

	/**
	 * Test method for
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase#addItem(com.keildraco.config.interfaces.ParserInternalTypeBase)}.
	 */
	@Test
	public final void testAddItem() {
		try {
			this.testItem.addItem(ParserInternalTypeBase.EMPTY_TYPE);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	/**
	 * Test method for {@link com.keildraco.config.interfaces.ParserInternalTypeBase#getParent()}.
	 */
	@Test
	public final void testGetParent() {
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, this.testItem.getParent());
	}

	@Test
	public final void testEmptyTypeGet() {
		assertNull(ParserInternalTypeBase.EMPTY_TYPE.get("blargh"));
	}

	@Test
	public final void testEmptyTypeGetType() {
		assertEquals(ParserInternalTypeBase.ItemType.EMPTY,
				ParserInternalTypeBase.EMPTY_TYPE.getType());
	}

	@Test
	public final void testEmptyTypeAddItem() {
		try {
			ParserInternalTypeBase.EMPTY_TYPE.addItem(ParserInternalTypeBase.EMPTY_TYPE);
			assertTrue(true, "Expected no exception");
		} catch (final Exception e) {
			fail("Exception (" + e.getMessage() + " :: " + e + ") caught when not expected");
		}
	}

	@Test
	public final void testParserInternalTypeBaseGetNoMember() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("z") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("blargh"));
	}

	@Test
	public final void testParserInternalTypeBaseGetValue() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("ZZTOP") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		assertEquals("", p.getValue());
	}

	@Test
	public final void testParserInternalTypeBaseGetChildrenEmpty() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("ZZTOP") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		assertEquals(Collections.emptyMap(), p.getChildren());
	}

	@Test
	public final void testParserInternalTypeBaseGetChildrenMembers() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		p.addItem(q);
		final Map<String, ParserInternalTypeBase> expectBase = new ConcurrentHashMap<>();
		expectBase.put("ZZTOP", q);
		assertEquals(Collections.unmodifiableMap(expectBase), p.getChildren());
	}
	
	@Test
	public final void testParserInternalTypeBaseGetItemLongNone() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("ZZTOP.MUZAK"));
	}

	@Test
	public final void testParserInternalTypeBaseGetItemLongValid() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		p.addItem(q);
		assertEquals(q, p.get("MUZAK.ZZTOP"));
	}

	@Test
	public final void testParserInternalTypeBaseGetItemLongCondTestOne() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("ZZTOP.ZZTOP"));
	}
	
	@Test
	public final void testParserInternalTypeBaseGetItemLongCondTestTwo() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("MUZAK.MUZAK"));
	}

	@Test
	public final void testParserInternalTypeBaseGetItemLongCondTestThree() {
		final ParserInternalTypeBase p = new ParserInternalTypeBase("MUZAK") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		final ParserInternalTypeBase q = new ParserInternalTypeBase("ZZTOP") {
			@Override
			public String getValue() {
				return "";
			}
			
			@Override
			public String asString() {
				return "Abstract!";
			}
			
			@Override
			public Number toNumber() {
				return Float.NaN;
			}
			
			@Override
			public boolean toBoolean() {
				return Boolean.FALSE;
			}
		};
		p.addItem(q);
		assertEquals(ParserInternalTypeBase.EMPTY_TYPE, p.get("BLARGH.BLECH"));
	}

}
