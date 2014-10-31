package com.readytalk.cultivar.discovery.payload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

@SuppressWarnings({ "ConstantConditions", "ObjectEqualsNull" })
public class ImmutablePropertiesTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private final ImmutableMap<String, String> props = ImmutableMap.of("key", "value");

    private ImmutableProperties immutableProperties;

    @Before
    public void setUp() {
        immutableProperties = ImmutableProperties.create(props);
    }

    @Test
    public void create_ValidMap_ReturnsNonNull() {
        assertNotNull(ImmutableProperties.create(props));
    }

    @Test
    public void create_NullMap_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        ImmutableProperties.create(null);
    }

    @Test
    public void create_MapContainsNullValue_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        Map<String, String> map = Maps.newHashMap();

        map.put("key", null);

        ImmutableProperties.create(map);
    }

    @Test
    public void create_MapContainsNullKey_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        Map<String, String> map = Maps.newHashMap();

        map.put(null, "value");

        ImmutableProperties.create(map);
    }

    @Test
    public void get_AbsentKey_ReturnsAbsent() {
        assertFalse("Value is present.", immutableProperties.get("notthere").isPresent());
    }

    @Test
    public void get_PresentKey_ReturnsValue() {
        assertEquals(props.get("key"), immutableProperties.get("key").get());
    }

    @Test
    public void get_WithDefault_PresentKey_ReturnsValue() {
        assertEquals(props.get("key"), immutableProperties.get("key", "def"));
    }

    @Test
    public void get_WithDefault_AbsentKey_ReturnsDefault() {
        assertEquals("def", immutableProperties.get("notthere", "def"));
    }

    @Test
    public void get_WithNullDefault_AbsentKey_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        immutableProperties.get("notthere", null);
    }

    @Test
    public void get_WithNullDefault_PresentKey_ReturnsValue() {

        assertEquals(props.get("key"), immutableProperties.get("key", null));
    }

    @Test
    public void equals_Null_ReturnsFalse() {
        assertFalse(immutableProperties.equals(null));
    }

    @Test
    public void equals_Self_ReturnsTrue() {
        assertTrue(immutableProperties.equals(immutableProperties));
    }

    @Test
    public void equals_DifferentObject_ReturnsFalse() {
        assertFalse(immutableProperties.equals(props));
    }

    @Test
    public void equals_Identical_ReturnsFalse() {
        assertTrue(immutableProperties.equals(ImmutableProperties.create(props)));
    }

    @Test
    public void hashCode_Identical_Equals() {
        assertEquals(immutableProperties.hashCode(), ImmutableProperties.create(props).hashCode());
    }

    @Test
    public void hashCode_DoesChange() {
        assertTrue("Hash codes are equal despite changing the underlying map.",
                immutableProperties.hashCode() != ImmutableProperties.create(ImmutableMap.of("key1", "value1"))
                        .hashCode());
    }

    @Test
    public void toString_ReturnsNotNull() {
        assertNotNull(immutableProperties.toString());
    }
}
