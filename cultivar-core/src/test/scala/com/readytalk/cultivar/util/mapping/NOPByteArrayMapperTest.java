package com.readytalk.cultivar.util.mapping;

import static org.junit.Assert.assertArrayEquals;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

@SuppressWarnings("ConstantConditions")
public class NOPByteArrayMapperTest {

    @Rule
    public final ExpectedException thrown = ExpectedException.none();

    private NOPByteArrayMapper mapper;

    @Before
    public void setUp() {
        mapper = new NOPByteArrayMapper();
    }

    @Test
    public void map_WithByteArray_ReturnsSelf() {
        byte[] bytes = new byte[] { 0x01, 0x02 };

        assertArrayEquals(bytes, mapper.map(bytes));
    }

    @Test
    public void map_Null_ThrowsNPE() {
        thrown.expect(NullPointerException.class);

        mapper.map(null);
    }
}
