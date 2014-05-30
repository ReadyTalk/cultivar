package com.readytalk.cultivar.util;

import java.util.Locale;
import java.util.Map;

import javax.annotation.Nullable;
import javax.annotation.concurrent.ThreadSafe;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

/**
 * Reads from system properties and, if the specified value isn't there, looks for something in environment variables.
 * 
 * Names are translated between environment and system properties by turning them lower case and substituting "_" for
 * "."
 */
@ThreadSafe
public final class PropertyReader {

    private static volatile ImmutableMap<String, String> properties = Maps.fromProperties(System.getProperties());

    private static volatile ImmutableMap<String, String> environment = ImmutableMap.copyOf(System.getenv());

    private PropertyReader() {

    }

    @Nullable
    public static String getProperty(final String key) {
        String retval = properties.get(key);

        if (retval == null) {
            String envKey = key.replace('.', '_').toUpperCase(Locale.getDefault());

            retval = environment.get(envKey);
        }

        return retval;
    }

    /**
     * 
     * @throws java.lang.NullPointerException
     *             if def is null and there is no property for the key.
     * @return The property if present, else the default specified value.
     */
    public static String getProperty(final String key, final String def) {
        return Objects.firstNonNull(getProperty(key), def);
    }

    @VisibleForTesting
    public static void setProperties(final Map<String, String> map) {
        properties = ImmutableMap.copyOf(map);
    }

    @VisibleForTesting
    public static void setEnvironment(final Map<String, String> map) {
        environment = ImmutableMap.copyOf(map);
    }

    /**
     * Resets to the current system properties and environment variables.
     */
    @VisibleForTesting
    public static void reset() {
        properties = Maps.fromProperties(System.getProperties());
        environment = ImmutableMap.copyOf(System.getenv());
    }

}
