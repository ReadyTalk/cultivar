package com.readytalk.cultivar.discovery.payload;

import java.util.Map;

import javax.annotation.concurrent.Immutable;

import org.codehaus.jackson.annotate.JsonAutoDetect;
import org.codehaus.jackson.annotate.JsonCreator;
import org.codehaus.jackson.annotate.JsonMethod;
import org.codehaus.jackson.annotate.JsonProperty;

import com.google.common.base.MoreObjects;
import com.google.common.base.Objects;
import com.google.common.base.Optional;
import com.google.common.collect.ImmutableMap;

/**
 * Provides a simple String:String mapping class to use as a payload for discovery purposes.
 */
@JsonAutoDetect(value = JsonMethod.FIELD, fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Immutable
public class ImmutableProperties {
    private final ImmutableMap<String, String> props;

    protected ImmutableProperties(final Map<String, String> props) {
        this.props = ImmutableMap.copyOf(props);
    }

    @JsonCreator
    public static ImmutableProperties create(@JsonProperty("props") final Map<String, String> props) {
        return new ImmutableProperties(props);
    }

    public Optional<String> get(final String key) {
        return Optional.fromNullable(props.get(key));
    }

    public String get(final String key, final String def) {
        return MoreObjects.firstNonNull(props.get(key), def);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(props);
    }

    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof ImmutableProperties)) {
            return false;
        }

        ImmutableProperties o = (ImmutableProperties) obj;

        return Objects.equal(props, o.props);
    }

    @Override
    public String toString() {
        return String.valueOf(props);
    }
}
