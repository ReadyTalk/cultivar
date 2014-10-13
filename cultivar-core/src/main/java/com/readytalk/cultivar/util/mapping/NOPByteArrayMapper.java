package com.readytalk.cultivar.util.mapping;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.annotations.Beta;
import com.google.inject.Inject;

/**
 * Returns a given byte array.
 */
@Beta
public class NOPByteArrayMapper extends AbstractByteArrayMapper<byte[]> {

    @Inject
    public NOPByteArrayMapper() {

    }

    @Override
    public byte[] map(final byte[] bytes) {
        return checkNotNull(bytes);
    }
}
