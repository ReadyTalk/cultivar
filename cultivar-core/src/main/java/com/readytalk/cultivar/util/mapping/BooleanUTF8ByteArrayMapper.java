package com.readytalk.cultivar.util.mapping;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Charsets;
import com.google.inject.Inject;

public class BooleanUTF8ByteArrayMapper extends AbstractByteArrayMapper<Boolean> {
    @Inject
    public BooleanUTF8ByteArrayMapper() {

    }

    @Override
    public Boolean map(final byte[] bytes) {

        return Boolean.valueOf(new String(checkNotNull(bytes), Charsets.UTF_8));
    }
}
