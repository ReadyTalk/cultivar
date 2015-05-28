package com.readytalk.cultivar.util.mapping;

import static com.google.common.base.Preconditions.checkNotNull;
import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Charsets;

public class AbstractByteArrayMapperTest {

    private final Object returnObject = new Object();

    private AbstractByteArrayMapper<Object> mapper;

    @Before
    public void setUp() throws Exception {
        mapper = new AbstractByteArrayMapper<Object>() {
            @Override
            public Object map(final byte[] bytes) {
                checkNotNull(bytes);
                return returnObject;
            }
        };
    }

    @Test
    public void map_NonNullParameter_WithNoDefault_ReturnObject() {
        assertEquals(returnObject, mapper.map("foo".getBytes(Charsets.UTF_8)));
    }

    @Test
    public void map_NullParameter_WithDefault_ReturnsDefault() {

        String value = "foo";
        assertEquals(value, mapper.map(null, value));
    }

    @Test
    public void map_NonNullParameter_WithDefault_ReturnObject() {
        String value = "foo";
        assertEquals(returnObject, mapper.map(value.getBytes(Charsets.UTF_8), value));
    }
}
