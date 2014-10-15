package com.readytalk.cultivar.discovery;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.google.common.annotations.Beta;

/**
 * Marker annotation for discovery-based instantiation.
 */
@Qualifier
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
@Beta
public @interface Discovery {
}
