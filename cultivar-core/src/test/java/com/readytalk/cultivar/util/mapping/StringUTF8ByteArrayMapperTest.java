package com.readytalk.cultivar.util.mapping;

import com.google.common.base.Charsets;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.junit.Assert.*;

@SuppressWarnings("ConstantConditions")
public class StringUTF8ByteArrayMapperTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private StringUTF8ByteArrayMapper mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new StringUTF8ByteArrayMapper();
    }

    @Test
    public void map_Null_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        mapper.map(null);
    }

    @Test
    public void map_String_ReturnsString() {
        String value = "foo";

        assertEquals(value, mapper.map(value.getBytes(Charsets.UTF_8)));
    }
}