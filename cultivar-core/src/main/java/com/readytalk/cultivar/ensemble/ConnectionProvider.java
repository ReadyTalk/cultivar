package com.readytalk.cultivar.ensemble;

import java.util.Map;
import java.util.Properties;

import com.readytalk.cultivar.internal.Private;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

/**
 * Provides the connection string based on either server properties or environment variables.
 */
@Beta
class ConnectionProvider implements Provider<Optional<String>> {

    static final String PROPERTY_NAME = "cultivar.zookeeper.connections";
    static final String ENVIRONMENT_NAME = "CULTIVAR_ZOOKEEPER_CONNECTIONS";

    private final Map<String, String> environment;
    private final Properties properties;

    private Optional<String> defaultValue = Optional.absent();

    @Inject
    ConnectionProvider(@Private final Properties properties, @Private final Map<String, String> environment) {
        this.environment = environment;
        this.properties = properties;
    }

    @Inject(optional = true)
    public void setDefault(@Named("Cultivar.zookeeper.connectionString") final String connectionString) {
        defaultValue = Optional.of(connectionString);
    }

    @Override
    public Optional<String> get() {

        return defaultValue.or(Optional.fromNullable(properties.getProperty(PROPERTY_NAME))).or(
                Optional.fromNullable(environment.get(ENVIRONMENT_NAME)));

    }
}
