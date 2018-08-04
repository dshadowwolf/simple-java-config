package com.keildraco.config.util;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import javax.annotation.Nullable;
import javax.annotation.meta.TypeQualifierDefault;

/**
 * @author Jasmine Iwanek
 *
 */
@Documented
@Nullable
@TypeQualifierDefault({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@SuppressWarnings("unused")
public @interface FieldsAreNullableByDefault {
}
