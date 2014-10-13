package com.readytalk.cultivar.util.mapping;


import com.google.common.annotations.Beta;
import com.google.common.base.Charsets;
import com.google.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

@Beta
public class StringUTF8ByteArrayMapper extends AbstractByteArrayMapper<String> {

    @Inject
    public StringUTF8ByteArrayMapper() {

    }

    @Override
    public String map(final byte[] bytes) {
        return new String(checkNotNull(bytes), Charsets.UTF_8);
    }
}
