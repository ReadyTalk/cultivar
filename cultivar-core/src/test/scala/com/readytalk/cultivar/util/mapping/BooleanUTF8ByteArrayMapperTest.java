package com.readytalk.cultivar.util.mapping;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.google.common.base.Charsets;

@SuppressWarnings("ConstantConditions")
public class BooleanUTF8ByteArrayMapperTest {
    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private BooleanUTF8ByteArrayMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new BooleanUTF8ByteArrayMapper();
    }

    @Test
    public void map_Null_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        mapper.map(null);
    }

    @Test
    public void map_WithTrueUTF8String_ReturnsTrue() {
        assertTrue(mapper.map("true".getBytes(Charsets.UTF_8)));
    }

    @Test
    public void map_WithFalseUTF8String_ReturnsFalse() {
        assertFalse(mapper.map("false".getBytes(Charsets.UTF_8)));
    }

    @Test
    public void map_WithUnknownUTF8String_ReturnsFalse() {
        assertFalse(mapper.map("1".getBytes(Charsets.UTF_8)));
    }
}
