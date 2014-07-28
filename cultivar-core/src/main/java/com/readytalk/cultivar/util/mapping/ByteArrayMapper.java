package com.readytalk.cultivar.util.mapping;

import javax.annotation.Nullable;

public interface ByteArrayMapper<T> {
    T map(byte[] bytes);

    T map(@Nullable byte[] bytes, T defaultValue);
}
