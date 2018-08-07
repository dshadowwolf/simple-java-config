package com.keildraco.config.data;

/**
 * <p>
 * Various (potentially frequently) repeated constants used throughout this code base.
 *
 * @author Daniel Hazelton
 *
 */
public final class Constants {

	/**
	 * Storage for the various names of the built-in parser states.
	 *
	 * @author Daniel Hazelton
	 *
	 */
	public static final class ParserNames {

		/**
		 * <p>
		 * Name of the parser state for handling key-value pairs.
		 */
		public static final String	KEYVALUE	= "KEYVALUE";
		/**
		 * <p>
		 * Name of the parser state for handling sections.
		 */
		public static final String	SECTION		= "SECTION";
		/**
		 * <p>
		 * Name of the parser state for handling operations.
		 */
		public static final String	OPERATION	= "OPERATION";
		/**
		 * <p>
		 * Name of the parser state for handling lists.
		 */
		public static final String	LIST		= "LIST";
		/**
		 * <p>
		 * Name of the root parser state that drives the entire machine.
		 */
		public static final String	ROOT		= "ROOT";

		/**
		 * Private constructor to hide the default public one.
		 */
		private ParserNames() {
			throw new IllegalAccessError("not an instantiable type");
		}
	}

	/**
	 * This is the splitter character used in ItemMatcher and some other places to split the
	 * incoming String into a set of separate item names for matching and lookup.
	 */
	public static final char	KEYSEPARATOR		= '.';
	/**
	 * <p>
	 * Used in ItemMatcher for work on checking if an OperationType matches a given value.
	 * </p>
	 * <p>
	 * (Question: Should this and IGNOREOPERATOR be in OperationType along with a generic match
	 * routine ? Perhaps spread through an abstract match in ParserInternalTypeBase to alleviate
	 * issues of ItemMatcher growing excessively complex ?)
	 * </p>
	 */
	public static final char	NOTOPERATOR			= '!';
	/**
	 * <p>
	 * Used in ItemMatcher for work on checking if an OperationType matches a given value.
	 * </p>
	 * <p>
	 * (Question: Should this and IGNOREOPERATOR be in OperationType along with a generic match
	 * routine ? Perhaps spread through an abstract match in ParserInternalTypeBase to alleviate
	 * issues of ItemMatcher growing excessively complex ?)
	 * </p>
	 */
	public static final char	IGNOREOPERATOR		= '~';
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * This is actually the separator between a key and value that is used by the ROOT and SECTION
	 * parsers to tell that a key value pair needs parsing.
	 */
	public static final String	STOREOPERATOR		= "=";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * This is the starting mark of a SECTION - used by the ROOT and SECTION parsers to know to
	 * shift to parsing a SECTION (or subsection).
	 */
	public static final String	OPENBRACE			= "{";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * Finding this is how the SECTION parser knows when to clean up and return.
	 */
	public static final String	CLOSEBRACE			= "}";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * Starting mark of an OPERATION - used by KEYVALUE and LIST parsers to know to shift to parsing
	 * an OPERATION.
	 */
	public static final String	OPENPARENS			= "(";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * When found by an OPERATION parser this says "clean up your work and return".
	 */
	public static final String	CLOSEPARENS			= ")";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * Start of a LIST, used by the KEYVALUE parser to know when to shift state to a LIST parser.
	 */
	public static final String	OPENLIST			= "[";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * End of a LIST - "clean up and return".
	 */
	public static final String	CLOSELIST			= "]";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * Separates different items in a list.
	 */
	public static final String	LISETSEPERATOR		= ",";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * Raw token values come into the {@link com.keildraco.config.data.Token Token} Constructor as
	 * {@link java.lang.String Strings} - this is a match for the "NOT" Operator.
	 */
	public static final String	NOTASSTRING			= "!";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to help decide what the next
	 * token will be based on the existing.
	 * <p>
	 * Raw token values come into the {@link com.keildraco.config.data.Token Token} Constructor as
	 * {@link java.lang.String Strings} - this is a match for the "IGNORE" Operator.
	 */
	public static final String	IGNOREASSTRING		= "~";
	/**
	 * <p>
	 * Used in the {@link com.keildraco.config.data.Token Token class} to define an Empty token.
	 * Finding this in a token stream actually means you've reached the end of input and have
	 * requested beyond that. This exists because returning null is a bad idea and we try not to use
	 * it anywhere in the code.
	 */
	public static final String	TOKENEMPTY			= "---EMPTY---";
	/**
	 * <p>
	 * Used as the value for the {@link com.keildraco.config.Config#EMPTY_TYPE "Empty"}
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase Parser Type} that replaces a
	 * null return or source value for any use of a generic Parser Type.
	 */
	public static final String	EMPTY_TYPE_VALUE	= "EMPTY";
	/**
	 * <p>
	 * Used as the name for the {@link com.keildraco.config.Config#EMPTY_TYPE "Empty"}
	 * {@link com.keildraco.config.interfaces.ParserInternalTypeBase Parser Type} that replaces a
	 * null return or source value for any use of a generic Parser Type.
	 * <p>
	 * While this is currently the same as {@link #EMPTY_TYPE_VALUE EMPTY_TYPE_VALUE} it doesn't
	 * have to be, hence the reason it is a separate constant.
	 */
	public static final String	EMPTY_TYPE_NAME		= EMPTY_TYPE_VALUE;
	/**
	 * <p>
	 * Currently only used in {@link BasicResult} this string represents the system default,
	 * when run through {@link java.lang.String#format(String, Object...) String.format()}, for
	 * a newline.
	 */
	public static final String NEWLINE_FORMAT_STRING = "%n";
	/**
	 * Private constructor to hide the default public one.
	 */
	private Constants() {
		throw new IllegalAccessError("not an instantiable type");
	}
}
