package com.readytalk.cultivar.util.mapping;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.inject.Inject;

/**
 * Returns a given byte array.
 */
public class NOPByteArrayMapper extends AbstractByteArrayMapper<byte[]> {

    @Inject
    public NOPByteArrayMapper() {

    }

    @Override
    public byte[] map(final byte[] bytes) {
        return checkNotNull(bytes);
    }
}
