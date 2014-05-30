package com.readytalk.cultivar.util;

import static org.junit.Assert.*;

import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class PropertyReaderTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        PropertyReader.setProperties(ImmutableMap.<String, String>of());
        PropertyReader.setEnvironment(ImmutableMap.<String, String>of());
    }

    @After
    public void tearDown() {
        PropertyReader.reset();
    }

    @Test
    public void getProperty_NoDefault_PropertyNorEnvironmentPresent_ReturnsNull() {
        assertNull(PropertyReader.getProperty("config.test"));
    }

    @Test
    public void getProperty_Default_PropertyNorEnvironmentPresent_ReturnsDefault() {
        assertEquals("foo", PropertyReader.getProperty("config.test", "foo"));
    }

    @Test
    public void getProperty_Default_NullDefaultNoPropertyPresent_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        PropertyReader.getProperty("config.test", null);
    }

    @Test
    public void getProperty_Default_NullDefaultPropertyPresent_ReturnsProperty() {
        PropertyReader.setProperties(ImmutableMap.of("config.test", "foo"));

        assertEquals("foo", PropertyReader.getProperty("config.test", null));
    }

    @Test
    public void getProperty_NoDefault_PropertyPresentAndEnvironmentPresent_ReturnsProperty() {
        PropertyReader.setProperties(ImmutableMap.of("config.test", "foo"));
        PropertyReader.setEnvironment(ImmutableMap.of("CONFIG_TEST", "bar"));

        assertEquals("foo", PropertyReader.getProperty("config.test"));
    }

    @Test
    public void getProperty_NoDefault_PropertyNotPresentAndEnvironmentPresent_ReturnsEnvironment() {
        PropertyReader.setEnvironment(ImmutableMap.of("CONFIG_TEST", "bar"));

        assertEquals("bar", PropertyReader.getProperty("config.test"));
    }

    @Test
    public void getProperty_NoDefault_NullKey_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        PropertyReader.getProperty(null);
    }
}