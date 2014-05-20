package com.readytalk.cultivar;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.google.common.annotations.Beta;

/**
 * Represents classes that interface directly with Curator. Generally used for configuration, returning Curator objects,
 * and for low-level details.
 */
@Qualifier
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
@Beta
public @interface Curator {
}
