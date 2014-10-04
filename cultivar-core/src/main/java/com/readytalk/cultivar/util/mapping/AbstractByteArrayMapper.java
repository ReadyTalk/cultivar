package com.readytalk.cultivar.util.mapping;

import com.google.common.annotations.Beta;

import javax.annotation.Nullable;

@Beta
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
