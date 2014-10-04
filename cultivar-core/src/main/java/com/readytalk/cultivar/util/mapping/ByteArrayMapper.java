package com.readytalk.cultivar.util.mapping;

import com.google.common.annotations.Beta;

import javax.annotation.Nullable;

@Beta
public interface ByteArrayMapper<T> {
    T map(byte[] bytes);

    T map(@Nullable byte[] bytes, T defaultValue);
}
