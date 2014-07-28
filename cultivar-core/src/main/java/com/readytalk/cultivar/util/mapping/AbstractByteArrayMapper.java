package com.readytalk.cultivar.util.mapping;

import javax.annotation.Nullable;

public abstract class AbstractByteArrayMapper<T> implements ByteArrayMapper<T> {

    protected AbstractByteArrayMapper() {

    }

    @Override
    public T map(@Nullable final byte[] bytes, final T defaultValue) {

        if (bytes == null) {
            return defaultValue;
        }

        return map(bytes);
    }
}
