package com.readytalk.cultivar.ensemble;

import java.util.Map;
import java.util.Properties;

import com.readytalk.cultivar.internal.Private;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;

@Beta
public class ExhibitorProvider implements Provider<Optional<String>> {

    static final String PROPERTY_NAME = "cultivar.zookeeper.exhibitor";
    static final String ENVIRONMENT_NAME = "CULTIVAR_ZOOKEEPER_EXHIBITOR";

    private final Map<String, String> environment;
    private final Properties properties;

    private Optional<String> defaultValue = Optional.absent();

    @Inject
    ExhibitorProvider(@Private final Properties properties, @Private final Map<String, String> environment) {
        this.environment = environment;
        this.properties = properties;
    }

    @Inject(optional = true)
    public void setDefault(@Named("Cultivar.zookeeper.exhibitorString") final String connectionString) {
        defaultValue = Optional.of(connectionString);
    }

    @Override
    public Optional<String> get() {
        return defaultValue.or(Optional.fromNullable(properties.getProperty(PROPERTY_NAME))).or(
                Optional.fromNullable(environment.get(ENVIRONMENT_NAME)));

    }
}
