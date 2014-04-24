package org.waterprinciple.cultivar.internal;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.PARAMETER;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import javax.inject.Qualifier;

import com.google.common.annotations.Beta;

/**
 * Used for bindings in private modules where the binding may already exist or where the final binding may not be known
 * until runtime.
 */
@Qualifier
@Target({ FIELD, PARAMETER, METHOD })
@Retention(RUNTIME)
@Beta
public @interface Private {
}
