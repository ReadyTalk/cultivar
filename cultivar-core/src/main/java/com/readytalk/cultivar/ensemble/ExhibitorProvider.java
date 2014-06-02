package com.readytalk.cultivar.ensemble;

import com.google.common.annotations.Beta;
import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.readytalk.cultivar.util.PropertyReader;

@Beta
public class ExhibitorProvider implements Provider<Optional<String>> {

    static final String PROPERTY_NAME = "config.cultivar.exhibitor";

    private Optional<String> defaultValue = Optional.absent();

    @Inject
    ExhibitorProvider() {
    }

    @Inject(optional = true)
    public void setDefault(@Named("Cultivar.zookeeper.exhibitorString") final String connectionString) {
        defaultValue = Optional.of(connectionString);
    }

    @Override
    public Optional<String> get() {
        return defaultValue.or(Optional.fromNullable(PropertyReader.getProperty(PROPERTY_NAME)));

    }
}
