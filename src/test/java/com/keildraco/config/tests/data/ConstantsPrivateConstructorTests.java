package com.keildraco.config.tests.data;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertAll;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import org.junit.jupiter.api.Test;


class ConstantsPrivateConstructorTests {

	@Test
	final void test() {
		Constructor<?> constantsConstructor = com.keildraco.config.data.Constants.class.getDeclaredConstructors()[0];
		Constructor<?> parserNamesConstructor = com.keildraco.config.data.Constants.ParserNames.class.getDeclaredConstructors()[0];
		constantsConstructor.setAccessible(true);
		parserNamesConstructor.setAccessible(true);

		// while the nominally private constructors throw an IllegalAccessError, because we're doing this through
		// reflection, that error is trapped and an InvocationTargetException exception is thrown by the reflection code
		// reflecting the fact that the Constructor in question has thrown an exception itself.
		assertAll(() -> assertThrows(InvocationTargetException.class, () -> constantsConstructor.newInstance()),
				() -> assertThrows(InvocationTargetException.class, () -> parserNamesConstructor.newInstance()));
	}

}
