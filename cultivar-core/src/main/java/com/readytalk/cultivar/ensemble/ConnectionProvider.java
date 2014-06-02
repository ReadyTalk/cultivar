package com.readytalk.cultivar.ensemble;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.readytalk.cultivar.util.PropertyReader;

/**
 * Provides the connection string based on either server properties or environment variables.
 */
@Beta
class ConnectionProvider implements Provider<Optional<String>> {

    static final String PROPERTY_NAME = "config.cultivar.zookeepers";

    private Optional<String> defaultValue = Optional.absent();

    @Inject
    ConnectionProvider() {
    }

    @Inject(optional = true)
    public void setDefault(@Named("Cultivar.zookeeper.connectionString") final String connectionString) {
        defaultValue = Optional.of(connectionString);
    }

    @Override
    public Optional<String> get() {

        return defaultValue.or(Optional.fromNullable(PropertyReader.getProperty(PROPERTY_NAME)));

    }
}
